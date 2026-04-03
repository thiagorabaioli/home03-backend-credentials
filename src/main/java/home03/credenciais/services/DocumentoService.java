package home03.credenciais.services;

import home03.credenciais.entities.Documento;
import home03.credenciais.repositories.DocumentoRepository;
import home03.credenciais.services.exceptions.BusinessException;
import home03.credenciais.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class DocumentoService {

    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
            "application/pdf", "image/jpeg", "image/png"
    );

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final DocumentoRepository documentoRepository;

    public DocumentoService(DocumentoRepository documentoRepository) {
        this.documentoRepository = documentoRepository;
    }

    public Documento guardar(MultipartFile ficheiro, UUID credencialId) {
        validarFicheiro(ficheiro);

        String nomeOriginal = ficheiro.getOriginalFilename();
        String extensao = obterExtensao(nomeOriginal);
        String nomeFicheiro = UUID.randomUUID() + extensao;
        String caminhoRelativo = credencialId + "/" + nomeFicheiro;

        Path destino = Paths.get(uploadDir, credencialId.toString());
        try {
            Files.createDirectories(destino);
            Files.copy(ficheiro.getInputStream(),
                    destino.resolve(nomeFicheiro),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BusinessException("Erro ao guardar o ficheiro: " + nomeOriginal);
        }

        Documento doc = new Documento(nomeOriginal, ficheiro.getContentType(),
                ficheiro.getSize(), caminhoRelativo);
        return documentoRepository.save(doc);
    }

    public Resource carregar(UUID documentoId) {
        Documento doc = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento não encontrado: " + documentoId));

        Path caminho = Paths.get(uploadDir, doc.getCaminhoFicheiro());
        try {
            Resource resource = new UrlResource(caminho.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("Ficheiro não encontrado no servidor: " + doc.getNomeOriginal());
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Caminho inválido para o documento: " + documentoId);
        }
    }

    public Documento buscarMetadata(UUID documentoId) {
        return documentoRepository.findById(documentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento não encontrado: " + documentoId));
    }

    public void eliminar(UUID documentoId) {
        Documento doc = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento não encontrado: " + documentoId));

        Path caminho = Paths.get(uploadDir, doc.getCaminhoFicheiro());
        try {
            Files.deleteIfExists(caminho);
        } catch (IOException e) {
            System.err.println("[DocumentoService] Erro ao eliminar ficheiro: " + caminho + " — " + e.getMessage());
        }
        documentoRepository.delete(doc);
    }

    private void validarFicheiro(MultipartFile ficheiro) {
        if (ficheiro == null || ficheiro.isEmpty()) {
            throw new BusinessException("Ficheiro vazio.");
        }
        if (!TIPOS_PERMITIDOS.contains(ficheiro.getContentType())) {
            throw new BusinessException("Tipo de ficheiro não permitido. Apenas PDF, JPEG e PNG são aceites.");
        }
    }

    private String obterExtensao(String nomeOriginal) {
        if (nomeOriginal != null && nomeOriginal.contains(".")) {
            return nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        }
        return "";
    }
}

package com_abertamente_cms.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * Armazena um arquivo e retorna o caminho ou URL para acessá-lo.
     * @param file O arquivo enviado
     * @param directory O subdiretório (ex: "avatars", "posts")
     * @return O caminho relativo ou URL absoluta do arquivo
     */
    String storeFile(MultipartFile file, String directory);

    /**
     * Remove um arquivo previamente armazenado
     * @param filePath O caminho ou URL retornada pelo storeFile
     */
    void deleteFile(String filePath);
}

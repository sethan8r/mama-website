package dev.sethan8r.mama.service;

import dev.sethan8r.mama.dto.PicturesWorksRequestDto;
import dev.sethan8r.mama.dto.PicturesWorksResponseAdminDto;
import dev.sethan8r.mama.dto.PicturesWorksResponseDto;
import dev.sethan8r.mama.exception.InvalidRequestException;
import dev.sethan8r.mama.exception.NotFoundException;
import dev.sethan8r.mama.mapper.PicturesWorksMapper;
import dev.sethan8r.mama.model.PicturesWorks;
import dev.sethan8r.mama.repository.PicturesWorksRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PicturesWorksService {

    private final PicturesWorksMapper picturesWorksMapper;
    private final PicturesWorksRepository picturesWorksRepository;

    @Transactional
    public void createPicturesWorks(PicturesWorksRequestDto dto, MultipartFile file) {
        log.info("Добавление примера работы с name={}", dto.name());

        if (file.isEmpty()) {
            throw new InvalidRequestException("Передан пустой файл");
        }

        PicturesWorks pic;
        try {
            pic = picturesWorksMapper.toPicturesWorks(dto, file.getBytes());
        } catch (IOException ex) {
            log.error("Ошибка чтения файла {}", file.getOriginalFilename(), ex);
            throw new InvalidRequestException("Не удалось прочитать файл изображения");
        }

        picturesWorksRepository.save(pic);
    }

    public Page<PicturesWorksResponseDto> getAllPicturesWorks(Pageable pageable) {
        log.info("Поиск всех примеров работ");

        Page<PicturesWorks> pages = picturesWorksRepository.findAll(pageable);

        return pages.map(picturesWorksMapper::toPicturesWorksResponseDto);
    }

    public Page<PicturesWorksResponseAdminDto> getAllPicturesWorksAdmin(Pageable pageable) {
        log.info("Поиск всех примеров работ для админа");

        Page<PicturesWorks> pages = picturesWorksRepository.findAll(pageable);

        return pages.map(picturesWorksMapper::toPicturesWorksResponseAdminDto);
    }

    public void deletePicturesWorks(Long pictureId) {
        log.info("Удаление примера работы с id={}", pictureId);

        picturesWorksRepository.deleteById(pictureId);
    }

    @Transactional
    public byte[] getPictureWorksById(Long pictureId) {
        log.info("Поиск картинки с id={}", pictureId);

        PicturesWorks pic = picturesWorksRepository.findById(pictureId).orElseThrow(
                () -> new NotFoundException("Картинка с id \"" + pictureId + "\" не найдена"));

        return pic.getImage();
    }
}

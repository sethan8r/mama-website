package dev.sethan8r.mama.service;

import dev.sethan8r.mama.dto.PicturesProductResponseAdminDto;
import dev.sethan8r.mama.dto.UploadPicturesResponseDto;
import dev.sethan8r.mama.exception.InvalidRequestException;
import dev.sethan8r.mama.exception.NotFoundException;
import dev.sethan8r.mama.mapper.PicturesProductMapper;
import dev.sethan8r.mama.model.PicturesProduct;
import dev.sethan8r.mama.model.Product;
import dev.sethan8r.mama.repository.PicturesProductRepository;
import dev.sethan8r.mama.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PicturesProductService {

    private final PicturesProductRepository picturesProductRepository;
    private final PicturesProductMapper picturesProductMapper;
    private final ProductRepository productRepository;

    @Transactional
    public UploadPicturesResponseDto createPicturesProductMany(Long productId, Integer[] sortOrders,
                                                               MultipartFile[] files) {
        log.info("Добавление множества картинок для продукта с id={} с очередностями {}",  productId, sortOrders);

        if(files.length != sortOrders.length) {
            throw new InvalidRequestException("Количество файлов (" + files.length
                    + ") не совпадает с количеством порядковых номеров (" + sortOrders.length + ")");
        }

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NotFoundException("Продукт с id \"" + productId + "\" не найден"));

        List<PicturesProduct> picturesProductsList = new ArrayList<>();

        int ok = 0;
        int length = files.length;

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];

            if (file.isEmpty()) {
                log.warn("Файл {} пустой — пропускаю", i);
                continue;
            }

            try {
                PicturesProduct pic = new PicturesProduct();
                pic.setProduct(product);
                pic.setSortOrder(sortOrders[i]);
                pic.setImage(file.getBytes());

                picturesProductsList.add(pic);
                ok++;

            } catch (IOException ex) {
                log.error("Ошибка чтения файла {}", file.getOriginalFilename(), ex);
                throw new InvalidRequestException("Не удалось прочитать файл изображения");
            }
        }

        picturesProductRepository.saveAll(picturesProductsList);

        return new UploadPicturesResponseDto(ok, length);
    }

    @Transactional
    public UploadPicturesResponseDto createPicturesProductBySlugMany(String slug, Integer[] sortOrders,
                                                               MultipartFile[] files) {
        log.info("Добавление множества картинок для продукта по slug={} с очередностями {}",  slug, sortOrders);

        if(files.length != sortOrders.length) {
            throw new InvalidRequestException("Количество файлов (" + files.length
                    + ") не совпадает с количеством порядковых номеров (" + sortOrders.length + ")");
        }

        Product product = productRepository.findBySlug(slug).orElseThrow(
                () -> new NotFoundException("Продукт со slug \"" + slug + "\" не найден"));

        List<PicturesProduct> picturesProductsList = new ArrayList<>();

        int ok = 0;
        int length = files.length;

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];

            if (file.isEmpty()) {
                log.warn("Файл {} пустой — пропускаю", i);
                continue;
            }

            try {
                PicturesProduct pic = new PicturesProduct();
                pic.setProduct(product);
                pic.setSortOrder(sortOrders[i]);
                pic.setImage(file.getBytes());

                picturesProductsList.add(pic);
                ok++;

            } catch (IOException ex) {
                log.error("Ошибка чтения файла {}", file.getOriginalFilename(), ex);
                throw new InvalidRequestException("Не удалось прочитать файл изображения");
            }
        }

        picturesProductRepository.saveAll(picturesProductsList);

        return new UploadPicturesResponseDto(ok, length);
    }

    @Transactional
    public void createPicturesProduct(Long productId, Integer sortOrders, MultipartFile file) {
        log.info("Добавление одно картинки для продукта с id={} и очередность {}",   productId, sortOrders);

        if (file.isEmpty()) {
            throw new InvalidRequestException("Передан пустой файл");
        }

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NotFoundException("Продукт с id \"" + productId + "\" не найден"));

        PicturesProduct pic = new PicturesProduct();
        try {
            pic.setProduct(product);
            pic.setSortOrder(sortOrders);
            pic.setImage(file.getBytes());
        } catch (IOException ex) {
            log.error("Ошибка чтения файла {}", file.getOriginalFilename(), ex);
            throw new InvalidRequestException("Не удалось прочитать файл изображения");
        }

        picturesProductRepository.save(pic);
    }

    @Transactional
    public void createPicturesProductBySlug(String slug, Integer sortOrders, MultipartFile file) {
        log.info("Добавление одно картинки для продукта со slug={} и очередность {}",   slug, sortOrders);

        if (file.isEmpty()) {
            throw new InvalidRequestException("Передан пустой файл");
        }

        Product product = productRepository.findBySlug(slug).orElseThrow(
                () -> new NotFoundException("Продукт с id \"" + slug + "\" не найден"));

        PicturesProduct pic = new PicturesProduct();
        try {
            pic.setProduct(product);
            pic.setSortOrder(sortOrders);
            pic.setImage(file.getBytes());
        } catch (IOException ex) {
            log.error("Ошибка чтения файла {}", file.getOriginalFilename(), ex);
            throw new InvalidRequestException("Не удалось прочитать файл изображения");
        }

        picturesProductRepository.save(pic);
    }

    @Transactional
    public void updateSortOrder(Long pictureId, Integer sortOrder) {
        log.info("Изменение порядка отображения для картинки с id={} на {}", pictureId, sortOrder);

        PicturesProduct pic = picturesProductRepository.findById(pictureId).orElseThrow(
                () -> new NotFoundException("Картинка с id \"" + pictureId + "\" не найдена"));
        pic.setSortOrder(sortOrder);

        picturesProductRepository.save(pic);
    }

    public List<PicturesProductResponseAdminDto> getPicturesProductByProductIdForAdmin(Long productId) {
        log.info("Поиск всех картинок админом для продукта с id={}",  productId);

        List<PicturesProduct> list = picturesProductRepository.findAllByProductIdOrderBySortOrderAsc(productId);

        return list.stream()
                .map(picturesProductMapper::toPicturesProductResponseAdminDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePicturesProductById(Long pictureId) {
        log.info("Удаление картинки с id={}", pictureId);

        picturesProductRepository.deleteById(pictureId);
    }

    public byte[] getPictureById(Long pictureId) {
        log.info("Поиск картинки с id={}", pictureId);

        PicturesProduct pic = picturesProductRepository.findById(pictureId).orElseThrow(
                () -> new NotFoundException("Картинка с id \"" + pictureId + "\" не найдена"));

        return pic.getImage();
    }
}

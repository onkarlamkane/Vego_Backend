package com.eptiq.vegobike.utils;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class ImageUtils {

    // Filesystem directory where images are stored (mapped by WebMvc to /uploads/**)
    // Examples: "/var/lib/uploads" or "uploads/images"
    private final String uploadDir;

    // Public base used to build browser URLs, e.g. "https://api.eptiq.com/uploads/"
    //@Value("${app.files.public-base-url:https://api.eptiq.com/uploads/}")
    @Value("${app.files.public-base-url:}")
    private String publicBaseUrl;

    // Supported image formats
    private static final List<String> SUPPORTED_FORMATS = Arrays.asList("jpg", "jpeg", "png", "webp");

    // Supported folders
    public static final String PROFILE_FOLDER = "profile";
    public static final String BRANDS_FOLDER = "brands";
    public static final String CATEGORIES_FOLDER = "categories";
    public static final String MODELS_FOLDER = "models";
    public static final String STORES_FOLDER = "stores";
    public static final String BIKES_FOLDER = "bikes";
    public static final String BIKE_SERVICES_FOLDER = "bike_services";
    public static final String BIKE_SALES_FOLDER = "bike-sales";
    public static final String CITIES_FOLDER = "cities";
    public static final String USER_DOCUMENTS_FOLDER = "user_documents";
    public static final String START_TRIP_FOLDER = "start_trip";
    public static final String END_TRIP_FOLDER = "end_trip";



    public ImageUtils(@Value("${image.upload-dir:uploads/images}") String uploadDir) {
        this.uploadDir = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
    }

    @PostConstruct
    public void init() {
        try {
            createDirectoryIfNotExists(PROFILE_FOLDER);
            createDirectoryIfNotExists(BRANDS_FOLDER);
            createDirectoryIfNotExists(CATEGORIES_FOLDER);
            createDirectoryIfNotExists(MODELS_FOLDER);
            createDirectoryIfNotExists(STORES_FOLDER);
            createDirectoryIfNotExists(BIKES_FOLDER);
            createDirectoryIfNotExists(BIKE_SERVICES_FOLDER);
            createDirectoryIfNotExists(BIKE_SALES_FOLDER);
            createDirectoryIfNotExists(CITIES_FOLDER);
            createDirectoryIfNotExists(USER_DOCUMENTS_FOLDER);
            createDirectoryIfNotExists(START_TRIP_FOLDER);
            createDirectoryIfNotExists(END_TRIP_FOLDER);
            log.info("IMAGE_UTILS - All upload directories initialized successfully");
        } catch (IOException e) {
            log.error("IMAGE_UTILS - Failed to create upload directories", e);
        }
    }

    // Generic store with sensible defaults (compression on for jpg/jpeg/png)
    public String storeImage(MultipartFile file, String folderName) throws IOException {
        return storeImage(file, folderName, true, 0.8, 1.0);
    }

    public String storeImage(MultipartFile file, String folderName, boolean compress, double quality, double scale) throws IOException {
        if (!isValidFolder(folderName)) {
            throw new IllegalArgumentException("Invalid folder name: " + folderName);
        }

        Path folderPath = createDirectoryIfNotExists(folderName);
        validateImageFile(file);

        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);
        String uniqueFileName = UUID.randomUUID() + "_" + System.currentTimeMillis() + "." + fileExtension;

        Path destination = folderPath.resolve(uniqueFileName).normalize();

        if (compress && (fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png"))) {
            compressAndSaveImage(file, destination, quality, scale, fileExtension);
        } else {
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        }

        String relativePath = folderName + "/" + uniqueFileName;
        log.info("IMAGE_UTILS - Stored image: {}", relativePath);
        return relativePath;
    }


    // Convenience specialized stores
    public String saveProfileImage(MultipartFile file, long userId) throws Exception {
        try {
            return storeImage(file, PROFILE_FOLDER, true, 0.8, 1.0);
        } catch (IOException e) {
            throw new Exception("Failed to save profile image: " + e.getMessage(), e);
        }
    }

    public String storeBrandImage(MultipartFile file) throws IOException {
        return storeImage(file, BRANDS_FOLDER, true, 0.85, 1.0);
    }

    public String storeCategoryImage(MultipartFile file) throws IOException {
        return storeImage(file, CATEGORIES_FOLDER, true, 0.85, 1.0);
    }

    public String storeModelImage(MultipartFile file) throws IOException {
        return storeImage(file, MODELS_FOLDER, true, 0.9, 1.0);
    }

    public String storeStoreImage(MultipartFile file) throws IOException {
        return storeImage(file, STORES_FOLDER, true, 0.85, 1.0);
    }

    public String storeCityImage(MultipartFile file) throws IOException {
        return storeImage(file, CITIES_FOLDER, true, 0.85, 1.0);
    }

    public String storeBikeImage(MultipartFile file) throws IOException {
        return storeImage(file, BIKES_FOLDER, true, 0.9, 1.0);
    }

    public String storeBikeServiceImage(MultipartFile file) throws IOException {
        return storeImage(file, BIKE_SERVICES_FOLDER, true, 0.85, 1.0);
    }

    public String storeBikeSaleImage(MultipartFile file) throws IOException {
        return storeImage(file, BIKE_SALES_FOLDER, true, 0.85, 1.0);
    }

    public String storeUserDocumentImage(MultipartFile file) throws IOException {
        return storeImage(file, USER_DOCUMENTS_FOLDER, true, 0.85, 1.0);
    }

    // Filesystem helpers
    public String getImagePath(String relativePath) {
        return uploadDir + relativePath;
    }

//    // Public URL builder (no cache-busting)
//    public String getPublicUrl(String relativePath) {
//        if (relativePath == null || relativePath.isBlank()) return null;
//        String base = publicBaseUrl.endsWith("/") ? publicBaseUrl : publicBaseUrl + "/";
//        String rel = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
//        return base + rel;
//    }

    // REPLACE getPublicUrl(...)
    public String getPublicUrl(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return null;
        String base = resolvePublicBase();
        String rel = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
        return ensureTrailingSlash(base) + rel;
    }


    // Last-modified millis of stored file (0 if missing/unreadable)
    public long getLastModifiedMillis(String relativePath) {
        try {
            Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path file = base.resolve(relativePath).normalize();
            if (!file.startsWith(base)) return 0L; // safety
            if (Files.exists(file)) return Files.getLastModifiedTime(file).toMillis();
        } catch (IOException e) {
            log.warn("IMAGE_UTILS - Could not read lastModified for {}", relativePath, e);
        }
        return 0L;
    }

    public String storeTripStartImage(MultipartFile file, String bookingId, int imageIndex) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Trip start image file is empty");
        }

        validateImageFile(file);

        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);

        // Create meaningful filename: booking_VEGO123_start_img1_1701234567890.jpg
        String fileName = "booking_" + bookingId + "_start_img" + imageIndex + "_" + System.currentTimeMillis() + "." + fileExtension;

        // **UPDATED** - Store directly in start_trip folder
        Path folderPath = createDirectoryIfNotExists(START_TRIP_FOLDER);
        Path destination = folderPath.resolve(fileName).normalize();

        // Store with good quality for trip images (0.85 quality, no scaling)
        compressAndSaveImage(file, destination, 0.85, 1.0, fileExtension);

        String relativePath = START_TRIP_FOLDER + "/" + fileName;
        log.info("IMAGE_UTILS - Stored trip start image: {} for booking: {}", relativePath, bookingId);

        return relativePath;
    }

    public String storeTripEndImage(MultipartFile file, String bookingId, int imageIndex) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Trip end image file is empty");
        }

        validateImageFile(file);

        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);

        // Create meaningful filename: booking_VEGO123_end_img1_1701234567890.jpg
        String fileName = "booking_" + bookingId + "_end_img" + imageIndex + "_" + System.currentTimeMillis() + "." + fileExtension;

        // **UPDATED** - Store directly in end_trip folder
        Path folderPath = createDirectoryIfNotExists(END_TRIP_FOLDER);
        Path destination = folderPath.resolve(fileName).normalize();

        // Store with good quality for trip images (0.85 quality, no scaling)
        compressAndSaveImage(file, destination, 0.85, 1.0, fileExtension);

        String relativePath = END_TRIP_FOLDER + "/" + fileName;
        log.info("IMAGE_UTILS - Stored trip end image: {} for booking: {}", relativePath, bookingId);

        return relativePath;
    }




    // Versioned public URL using ?v=<lastModifiedMillis>
    public String getPublicUrlVersioned(String relativePath) {
        String url = getPublicUrl(relativePath);
        if (url == null) return null;
        long v = getLastModifiedMillis(relativePath);
        return v > 0 ? (url + (url.contains("?") ? "&" : "?") + "v=" + v) : url;
    }

    // Delete file by relative path
    public boolean deleteImage(String relativePath) {
        try {
            Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path filePath = base.resolve(relativePath).normalize();
            if (!filePath.startsWith(base)) return false;
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) log.info("IMAGE_UTILS - Deleted image: {}", relativePath);
            return deleted;
        } catch (IOException e) {
            log.error("IMAGE_UTILS - Failed to delete image: {}", relativePath, e);
            return false;
        }
    }

    // Internal utilities

    private Path createDirectoryIfNotExists(String folderName) throws IOException {
        Path folderPath = Paths.get(uploadDir, folderName).toAbsolutePath().normalize();
        if (Files.notExists(folderPath)) {
            Files.createDirectories(folderPath);
            log.info("IMAGE_UTILS - Created directory: {}", folderPath);
        }
        return folderPath;
    }

    private boolean isValidFolder(String folderName) {
        return Arrays.asList(
                PROFILE_FOLDER, BRANDS_FOLDER, CATEGORIES_FOLDER, MODELS_FOLDER, STORES_FOLDER,
                BIKES_FOLDER, BIKE_SERVICES_FOLDER, BIKE_SALES_FOLDER, CITIES_FOLDER , USER_DOCUMENTS_FOLDER,
                START_TRIP_FOLDER, END_TRIP_FOLDER
        ).contains(folderName);
    }

    private void validateImageFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IOException("File is empty");
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || !originalFileName.contains(".")) throw new IOException("Invalid file name");
        String fileExtension = getFileExtension(originalFileName);
        if (!SUPPORTED_FORMATS.contains(fileExtension.toLowerCase())) {
            throw new IOException("Unsupported image format: " + fileExtension + ". Supported: " + SUPPORTED_FORMATS);
        }
        if (!fileExtension.equalsIgnoreCase("webp")) {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) throw new IOException("Invalid image file (not readable)");
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }

    private void compressAndSaveImage(MultipartFile file, Path destination,
                                      double quality, double scale, String format) throws IOException {
        if (format.equalsIgnoreCase("webp")) {
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return;
        }
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) throw new IOException("Unable to read image for compression");
        try (OutputStream os = Files.newOutputStream(destination)) {
            Thumbnails.of(image)
                    .scale(scale)
                    .outputQuality(quality)
                    .outputFormat(format)
                    .toOutputStream(os);
        }
    }


    // ADD helpers
    private String resolvePublicBase() {
        if (StringUtils.hasText(publicBaseUrl)) {
            return ensureUploadsBase(publicBaseUrl);
        }
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest req = attrs.getRequest();
            String scheme = req.getScheme();
            String host = req.getServerName();
            int port = req.getServerPort();
            boolean defaultPort = ("http".equalsIgnoreCase(scheme) && port == 80)
                    || ("https".equalsIgnoreCase(scheme) && port == 443);
            String origin = scheme + "://" + host + (defaultPort ? "" : ":" + port);
            return ensureUploadsBase(origin + "/");
        }
        return "/uploads/";
    }

    private static String ensureUploadsBase(String base) {
        String b = ensureTrailingSlash(base);
        return b.endsWith("/uploads/") ? b : (b + "uploads/");
    }

    private static String ensureTrailingSlash(String s) {
        return s.endsWith("/") ? s : s + "/";
    }

    public String storeUserDocument(MultipartFile file, int userId, String documentType) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Document file is empty");
        }

        validateImageFile(file); // Reuse existing validation

        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);

        // Create meaningful filename: user_123_adhaar_front_1701234567890.jpg
        String fileName = "user_" + userId + "_" + documentType + "_" + System.currentTimeMillis() + "." + fileExtension;

        // Store in user_documents folder
        Path folderPath = createDirectoryIfNotExists(USER_DOCUMENTS_FOLDER);
        Path destination = folderPath.resolve(fileName).normalize();

        // Store with good quality for documents (0.9 quality, no scaling)
        compressAndSaveImage(file, destination, 0.9, 1.0, fileExtension);

        String relativePath = USER_DOCUMENTS_FOLDER + "/" + fileName;
        log.info("IMAGE_UTILS - Stored user document: {} for user: {}", relativePath, userId);

        return relativePath;
    }

    public String storeAadhaarFrontImage(MultipartFile file, int userId) throws IOException {
        return storeUserDocument(file, userId, "adhaar_front");
    }

    public String storeAadhaarBackImage(MultipartFile file, int userId) throws IOException {
        return storeUserDocument(file, userId, "adhaar_back");
    }

    public String storeDrivingLicenseImage(MultipartFile file, int userId) throws IOException {
        return storeUserDocument(file, userId, "driving_license");
    }
}


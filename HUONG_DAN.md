# Build APK qua GitHub (không cần Android Studio)

1. Vào github.com > New repository (đặt tên tùy ý, ví dụ khai-niem) > Create.
2. Chọn "uploading an existing file", kéo TOÀN BỘ nội dung thư mục này vào > Commit changes.
   Nếu thư mục ẩn `.github` không được tải lên: Add file > Create new file, gõ tên
   `.github/workflows/build.yml`, dán nội dung file `workflow-backup.yml` > Commit.
3. Mở tab Actions, đợi "Build APK" chạy xong (khoảng 5-10 phút). Chưa chạy thì bấm Run workflow.
4. Về trang chính của repo, mục Releases (cột bên phải) > "Bản mới nhất" > tải `app-debug.apk` về điện thoại > cài.
   (Máy sẽ hỏi cho phép cài từ nguồn không xác định.)
5. Mở app > bấm "Khóa API" > dán khóa Claude API (sk-ant-...) > Lưu.

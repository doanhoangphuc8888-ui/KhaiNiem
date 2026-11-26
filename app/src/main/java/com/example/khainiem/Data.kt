package com.example.khainiem

data class Concept(
    val name: String, val category: String, val definition: String,
    val details: List<String>, val example: String, val related: List<String>,
    val fromAi: Boolean = false
)
data class DiffRow(val aspect: String, val a: String, val b: String)
data class Comparison(
    val a: String, val b: String, val summary: String, val similarities: List<String>,
    val differences: List<DiffRow>, val example: String, val conclusion: String
)

val CATEGORIES = listOf("Đạo đức", "Tâm lý", "Triết học", "Tôn giáo")

private fun c(n: String, cat: String, d: String, det: List<String>, ex: String, rel: List<String>) =
    Concept(n, cat, d, det, ex, rel)

val BUILT_IN = listOf(
    c("Lòng trắc ẩn", "Đạo đức",
        "Sự rung động trước nỗi đau của người khác, kèm mong muốn làm họ vơi bớt khổ.",
        listOf("Gồm hai phần: nhận ra nỗi đau và muốn hành động để giảm nhẹ", "Không đòi hỏi mình từng trải qua đúng điều đó", "Mạnh Tử xem “trắc ẩn chi tâm” là mầm của đức Nhân"),
        "Thấy người vô gia cư run rẩy giữa trời lạnh, bạn dừng lại hỏi han và mời họ bữa ăn nóng.",
        listOf("Lòng tốt", "Sự đồng cảm", "Lòng thương hại", "Từ bi")),
    c("Lòng tốt", "Đạo đức",
        "Khuynh hướng đối xử tử tế, mong điều lành cho người khác và sẵn lòng giúp đỡ.",
        listOf("Có thể xuất phát từ tính cách hay phép lịch sự, không nhất thiết cần thấy ai đó đau khổ", "Thể hiện được cả khi người kia đang vui", "Thiên về phẩm chất ổn định hơn là phản ứng trước một hoàn cảnh"),
        "Nhường ghế cho người lớn tuổi, mua cà phê tặng đồng nghiệp trong một ngày bình thường.",
        listOf("Lòng trắc ẩn", "Nhân (Nho giáo)")),
    c("Sự đồng cảm", "Tâm lý",
        "Khả năng nhận biết và cảm nhận trạng thái cảm xúc của người khác.",
        listOf("Thường chia thành đồng cảm nhận thức (hiểu) và đồng cảm cảm xúc (cảm theo)", "Tự nó chưa chắc dẫn đến hành động giúp đỡ", "Đồng cảm quá mức có thể gây kiệt sức cảm xúc"),
        "Nghe bạn kể trượt phỏng vấn, bạn thấy nặng lòng như chính mình bị trượt.",
        listOf("Lòng trắc ẩn", "Lòng thương hại")),
    c("Lòng thương hại", "Đạo đức",
        "Cảm giác xót xa cho người khác, nhưng thường từ vị thế “bên trên” và giữ khoảng cách.",
        listOf("Có thể hàm ý coi người kia yếu thế hơn mình", "Ít sự kết nối và bình đẳng hơn lòng trắc ẩn", "Nietzsche phê phán thương hại vì cho rằng nó làm yếu cả người cho lẫn người nhận"),
        "Nhìn ai đó vấp ngã và thầm nghĩ “tội nghiệp, may mà mình không như vậy”.",
        listOf("Lòng trắc ẩn", "Sự đồng cảm")),
    c("Từ bi", "Tôn giáo",
        "Trong Phật giáo: Từ là mong chúng sinh được an vui, Bi là mong chúng sinh thoát khổ.",
        listOf("Từ (mettā) và Bi (karuṇā) thuộc Tứ vô lượng tâm, cùng Hỷ và Xả", "Hướng đến mọi chúng sinh, không phân biệt thân sơ", "Đi cùng trí tuệ, tránh thương cảm mù quáng"),
        "Thiền từ bi: lần lượt gửi lời chúc an lành đến bản thân, người thân, người xa lạ, cả người mình không ưa.",
        listOf("Lòng trắc ẩn", "Vô ngã")),
    c("Vô ngã", "Tôn giáo",
        "Quan điểm Phật giáo cho rằng không có một “cái tôi” thường hằng, độc lập; con người là tập hợp năm uẩn luôn đổi thay.",
        listOf("Một trong Tam pháp ấn: vô thường, khổ, vô ngã", "Năm uẩn: sắc, thọ, tưởng, hành, thức", "Không có nghĩa là “không tồn tại” mà là không có bản thể cố định"),
        "Cơn giận đến rồi đi; quan sát nó, bạn thấy “tôi đang giận” không phải một thực thể bất biến.",
        listOf("Từ bi")),
    c("Chủ nghĩa khắc kỷ", "Triết học",
        "Trường phái Hy Lạp - La Mã dạy rằng hạnh phúc đến từ sống theo lý trí và đức hạnh, chấp nhận điều ngoài tầm kiểm soát.",
        listOf("Đại diện: Zeno, Epictetus, Seneca, Marcus Aurelius", "Phân biệt điều trong tầm kiểm soát (phán đoán, hành động) và ngoài tầm kiểm soát", "Cảm xúc tiêu cực bắt nguồn từ phán đoán sai"),
        "Kẹt xe làm trễ hẹn: bạn không kiểm soát được kẹt xe, chỉ kiểm soát cách mình phản ứng.",
        listOf("Vô vi", "Chủ nghĩa hiện sinh")),
    c("Chủ nghĩa hiện sinh", "Triết học",
        "Trào lưu nhấn mạnh sự tồn tại cá nhân, tự do và trách nhiệm tự tạo ý nghĩa cho đời mình.",
        listOf("Sartre: “tồn tại có trước bản chất”", "Gắn với Kierkegaard, Sartre, Camus, de Beauvoir", "Chủ đề: tự do, lo âu, tính phi lý, lựa chọn"),
        "Không ai định sẵn nghề nghiệp hay lẽ sống cho bạn; bạn phải chọn và chịu trách nhiệm.",
        listOf("Chủ nghĩa khắc kỷ")),
    c("Nhân (Nho giáo)", "Triết học",
        "Đức tính cốt lõi của Nho giáo: lòng yêu thương con người, thể hiện qua cách ứng xử đạo đức trong các mối quan hệ.",
        listOf("Khổng Tử giải thích nhân là “ái nhân” (yêu người)", "Mạnh Tử cho rằng nhân bắt nguồn từ tâm trắc ẩn bẩm sinh", "Đứng đầu Ngũ thường: Nhân, Nghĩa, Lễ, Trí, Tín"),
        "Đối xử với cấp dưới bằng sự tôn trọng và quan tâm thật lòng.",
        listOf("Lòng trắc ẩn", "Lòng tốt")),
    c("Vô vi", "Tôn giáo",
        "Nguyên lý của Đạo giáo: sống thuận theo tự nhiên, không cưỡng cầu, không can thiệp thái quá.",
        listOf("Xuất phát từ Đạo Đức Kinh của Lão Tử", "Không phải “không làm gì” mà là hành động thuận theo thời thế", "Hình ảnh quen thuộc: nước mềm mại nhưng thắng được cái cứng"),
        "Thay vì ép một cuộc trò chuyện đi theo ý mình, bạn lắng nghe và để nó diễn ra tự nhiên.",
        listOf("Chủ nghĩa khắc kỷ"))
)

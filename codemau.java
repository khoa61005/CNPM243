import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.UUID;

public class PersonalTaskManagerViolations {
    private static final String TASKS_FILE = "tasks.json";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            System.out.println("========= QUẢN LÝ NHIỆM VỤ =========");
            System.out.println("1. Thêm nhiệm vụ mới");
            System.out.println("2. Hiển thị tất cả nhiệm vụ");
            System.out.println("3. Thoát");
            System.out.print("Chọn: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addNewTaskWithViolations(scanner);
                    break;
                case "2":
                    showAllTasks();
                    break;
                case "3":
                    exit = true;
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ.");
            }
        }
        scanner.close();
    }

    private static void addNewTaskWithViolations(Scanner scanner) {
        System.out.println("----- Thêm Nhiệm Vụ -----");
        System.out.print("Tiêu đề: ");
        String title = scanner.nextLine();

        System.out.print("Mô tả: ");
        String description = scanner.nextLine();

        System.out.print("Ngày hết hạn (yyyy-MM-dd): ");
        String dueDateStr = scanner.nextLine();
        LocalDate dueDate = LocalDate.parse(dueDateStr, DATE_FORMATTER);

        System.out.print("Mức độ ưu tiên (Cao/Trung bình/Thấp): ");
        String priorityLevel = scanner.nextLine();

        String taskId = UUID.randomUUID().toString();

        JSONObject newTask = createTaskObject(taskId, title, description, dueDate, priorityLevel);

        JSONArray tasks = loadTasks();
        tasks.put(newTask);
        saveTasks(tasks);

        System.out.println("✅ Nhiệm vụ đã được thêm.");
    }

    private static JSONObject createTaskObject(String id, String title, String description, LocalDate dueDate, String priorityLevel) {
        JSONObject task = new JSONObject();
        task.put("id", id);
        task.put("title", title);
        task.put("description", description);
        task.put("due_date", dueDate.format(DATE_FORMATTER));
        task.put("priority", priorityLevel);
        task.put("status", "Chưa hoàn thành");
        task.put("created_at", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        task.put("last_updated_at", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        return task;
    }

    private static JSONArray loadTasks() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(TASKS_FILE)));
            return new JSONArray(content);
        } catch (IOException e) {
            return new JSONArray();
        }
    }

    private static void saveTasks(JSONArray tasks) {
        try (FileWriter file = new FileWriter(TASKS_FILE)) {
            file.write(tasks.toString(2));
        } catch (IOException e) {
            System.out.println("❌ Lỗi ghi file: " + e.getMessage());
        }
    }

    private static void showAllTasks() {
        JSONArray tasks = loadTasks();
        System.out.println("----- Danh sách nhiệm vụ -----");
        for (int i = 0; i < tasks.length(); i++) {
            JSONObject task = tasks.getJSONObject(i);
            System.out.println("ID: " + task.getString("id"));
            System.out.println("Tiêu đề: " + task.getString("title"));
            System.out.println("Mô tả: " + task.getString("description"));
            System.out.println("Hạn chót: " + task.getString("due_date"));
            System.out.println("Ưu tiên: " + task.getString("priority"));
            System.out.println("Trạng thái: " + task.getString("status"));
            System.out.println("Ngày tạo: " + task.getString("created_at"));
            System.out.println("Cập nhật gần nhất: " + task.getString("last_updated_at"));
            System.out.println("----------------------------");
        }
    }
}

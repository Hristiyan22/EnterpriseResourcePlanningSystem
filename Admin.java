import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class Admin extends User implements ClientManagement, EmployeeManagement {
    private Map<String, Project> projects;
    private List<Client> clients;
    private List<Employee> employees;
    private List<String> registeredEmployees;
    private boolean isAdmin;

    public Admin(String username, String password) {
        super(username, password);
        this.projects = new HashMap<>();
        this.clients = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.registeredEmployees = new ArrayList<>();
        this.isAdmin = true;
    }
    public List<String> getRegisteredEmployees() {
        return registeredEmployees;
    }
    @Override
    public void saveDataToFile(String filePath) {
        try (PrintWriter writer = new PrintWriter("registered_employees.csv")) {
            writer.println(username + "," + password);
            System.out.println("Data saved to file: " + "D:\\Java\\Модул 2\\TimeTrackingSystem\\src\\registered_employees.csv");//добави път към файла
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadDataFromFile(String filePath) {
        try (Scanner scanner = new Scanner(new File("registered_employees.csv"))) {
            username = scanner.next();
            password = scanner.next();
            System.out.println("Data loaded from file: " + "D:\\Java\\Модул 2\\TimeTrackingSystem\\src\\registered_employees.csv");//добави път към файла
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addClient(String name, String projectName, String contractExpirationDate) throws ParseException {
        Date expirationDate = new SimpleDateFormat("dd-mm-yyyy").parse(contractExpirationDate);
        clients.add(new Client(name, generateRandomPassword(), projectName, expirationDate));
        System.out.println("Client " + name + " added successfully.");
        System.out.println(" ");
    }

    public void registerEmployee(String username, String password) {
        if (!isUsernameUnique(username)) {
            System.out.println("Username " + username + " is not unique. Please choose a different username.");
            return;
        }

        employees.add(new Employee(username, password));
        registeredEmployees.add(username);
        System.out.println("Employee with username " + username + " registered successfully.");
        System.out.println(" ");

        // Save the registered employees' information automatically
        saveRegisteredEmployeesToFile("D:\\Java\\Модул 2\\TimeTrackingSystem\\src\\registered_employees.csv");
    }
    public boolean login(String enteredUsername, String enteredPassword) {
        return username.equals(enteredUsername) && password.equals(enteredPassword);
    }
    public void saveRegisteredEmployeesToFile(String filePath) {
        try (PrintWriter writer = new PrintWriter("registered_employees.csv")) {
            for (String employeeUsername : registeredEmployees) {
                writer.println(employeeUsername);
            }
            System.out.println("Registered employees' information saved to file: " + "D:\\Java\\Модул 2\\TimeTrackingSystem\\src\\registered_employees.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadRegisteredEmployeesFromFile(String filePath) {
        try (Scanner scanner = new Scanner(new File("D:\\Java\\Модул 2\\TimeTrackingSystem\\src\\registered_employees.csv"))) {
            while (scanner.hasNextLine()) {
                String employeeUsername = scanner.nextLine().trim(); // Trim to remove leading/trailing spaces
                registeredEmployees.add(employeeUsername);
            }
            System.out.println("Registered employees' information loaded from file: " + "D:\\Java\\Модул 2\\TimeTrackingSystem\\src\\registered_employees.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean isUsernameUnique(String username) {
        return !registeredEmployees.contains(username);
    }

    public void AdminMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Continue with admin tasks");
            System.out.println("2. Go back to login");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    AdminMenu adminMenuHandler = new AdminMenu(this, new Scanner(System.in));
                    adminMenuHandler.handleAdminMenu();
                    break;
                case 2:
                    System.out.println("Going back to login...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    public void createProject(String projectName, int initialHours) {
        projects.put(projectName, new Project(projectName, initialHours));
        System.out.println("Project " + projectName + " was created successfully.");
    }

    public void displayProjects() {
        System.out.println("List of projects:");
        Set<Map.Entry<String, Project>> entries = projects.entrySet();
        Iterator<Map.Entry<String, Project>> iterator = entries.iterator();
        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<String, Project> entry = iterator.next();
            Project project = entry.getValue();
            System.out.println(project.getProjectName() + ": " + project.getTotalHours() + " hours");
        }
    }

    public void displayStatisticsByName(String employeeName) {
        for (int i = 0; i < employees.size(); i++) {
            Employee employee = employees.get(i);
            if (employee.getUsername().equals(employeeName)) {
                System.out.println("Employee Statistics: " + employeeName);
                System.out.println("Projects:");
                List<String> projects = employee.getProjects();
                for (int j = 0; j < projects.size(); j++) {
                    System.out.println("- " + projects.get(j));
                }
                System.out.println("Total Hours Worked: " + employee.getTotalHoursWorked());
                break;
            }
        }
    }

    public void displayStatisticsByWeek(int weekNumber) {
        System.out.println("Statistics for Week " + weekNumber);
        for (int i = 0; i < employees.size(); i++) {
            Employee employee = employees.get(i);
            int hoursWorked = employee.getHoursWorkedByWeek(weekNumber);
            System.out.println(employee.getUsername() + ": " + hoursWorked + " hours");
        }
    }

    public void displayEmployeeStatisticsMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1. Search by employee name");
        System.out.println("2. Search by week number");
        System.out.println("0. Back");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                // Search by employee name
                System.out.print("Enter employee name: ");
                String employeeName = scanner.nextLine();
                displayStatisticsByName(employeeName);
                break;
            case 2:
                // Search by week number
                System.out.print("Enter week number: ");
                int weekNumber = scanner.nextInt();
                scanner.nextLine();
                displayStatisticsByWeek(weekNumber);
                break;
            case 0:
                // Back
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    private String generateRandomPassword() {
        // Generate a random password (can be implemented more securely)
        return UUID.randomUUID().toString().substring(0, 8);
    }//виж защо

}

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Main {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres"; // База данных postgres
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "postgres"; // Укажите правильный пароль

    private Connection connection;

    public Main() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            JOptionPane.showMessageDialog(null, "Connected to database successfully!");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
        }
    }

    public boolean isLoginValid(String username, String password) {
        return username.equals(DB_USER) && password.equals(DB_PASS);
    }

    public void addFlower(String name, double value, String category) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT add_flower(?, CAST(? AS NUMERIC), ?)")) {
            stmt.setString(1, name);
            stmt.setDouble(2, value);  // Передаём число как double
            stmt.setString(3, category);
            stmt.execute();
            JOptionPane.showMessageDialog(null, "Flower added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding flower: " + e.getMessage());
        }
    }


    public void searchFlower(String name) {
        String sql = "SELECT * FROM search_flower(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Flower: " + rs.getString("name") +
                        ", Value: " + rs.getDouble("value") +
                        ", Category: " + rs.getString("category"));
            } else {
                JOptionPane.showMessageDialog(null, "Flower not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error searching flower: " + e.getMessage());
        }
    }

    public void deleteFlower(String name) {
        String sql = "SELECT delete_flower(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.execute();
            JOptionPane.showMessageDialog(null, "Flower deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting flower: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::showLoginScreen);
    }

    private static void showLoginScreen() {
        JFrame frame = new JFrame("Flower Shop - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        JPanel panel = new JPanel(new GridLayout(3, 2));

        JLabel userLabel = new JLabel("Username:");
        JTextField userText = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordText = new JPasswordField(20);
        JButton loginButton = new JButton("Login");

        panel.add(userLabel);
        panel.add(userText);
        panel.add(passwordLabel);
        panel.add(passwordText);
        panel.add(new JLabel());
        panel.add(loginButton);

        frame.add(panel);
        frame.setVisible(true);

        loginButton.addActionListener(e -> {
            String user = userText.getText();
            String password = new String(passwordText.getPassword());
            Main app = new Main();

            if (app.isLoginValid(user, password)) {
                JOptionPane.showMessageDialog(null, "Login successful!");
                frame.dispose();
                showMainMenu(app);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid login credentials!");
            }
        });
    }

    private static void showMainMenu(Main app) {
        JFrame mainFrame = new JFrame("Flower Shop - Management");
        mainFrame.setSize(500, 400);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Flower Name:");
        JTextField nameField = new JTextField(15);
        JLabel valueLabel = new JLabel("Value:");
        JTextField valueField = new JTextField(10);
        JLabel categoryLabel = new JLabel("Category:");
        JTextField categoryField = new JTextField(15);

        JButton addButton = new JButton("Add Flower");
        JButton searchButton = new JButton("Search Flower");
        JButton deleteButton = new JButton("Delete Flower");

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(valueLabel, gbc);
        gbc.gridx = 1;
        panel.add(valueField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(categoryLabel, gbc);
        gbc.gridx = 1;
        panel.add(categoryField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(addButton, gbc);
        gbc.gridx = 1;
        panel.add(searchButton, gbc);
        gbc.gridx = 2;
        panel.add(deleteButton, gbc);

        mainFrame.add(panel);
        mainFrame.setVisible(true);

        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                double value = Double.parseDouble(valueField.getText());
                String category = categoryField.getText();
                app.addFlower(name, value, category);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid value value!");
            }
        });

        searchButton.addActionListener(e -> {
            String name = nameField.getText();
            app.searchFlower(name);
        });

        deleteButton.addActionListener(e -> {
            String name = nameField.getText();
            app.deleteFlower(name);
        });
    }
}

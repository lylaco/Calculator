package calc;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Calculator extends Application {
    private TextField display;
    private double firstOperand = 0;
    private String pendingOperator = "";
    private boolean startNewInput = true;

    @Override
    public void start(Stage primaryStage) {
        display = new TextField("0");
        display.setEditable(false);
        display.setAlignment(Pos.CENTER_RIGHT);

        String[][] buttonLabels = {
            {"C", "⌫", "/", "*"}, {"7", "8", "9", "-"},
            {"4", "5", "6", "+"}, {"1", "2", "3", "="}, {"0"}
        };

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(10));

        for (int r = 0; r < buttonLabels.length; r++) {
            for (int c = 0; c < buttonLabels[r].length; c++) {
                String label = buttonLabels[r][c];
                Button btn = new Button(label);
                btn.setPrefSize(60, 50);
                btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                btn.setOnAction(e -> handleButtonClick(label));

                if (label.equals("=")) grid.add(btn, c, r, 2, 1);
                else grid.add(btn, c, r);
            }
        }

        VBox root = new VBox(10, display, grid);
        root.setPadding(new Insets(15));
        primaryStage.setTitle("Calculator");
        primaryStage.setScene(new Scene(root, 320, 430));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void handleButtonClick(String value) {
        if ("0123456789".contains(value)) {
            if (startNewInput) {
                display.setText(!pendingOperator.isEmpty() ? display.getText() + value : value);
                startNewInput = false;
            } else {
                display.setText(display.getText() + value);
            }
        } else if (value.equals("C")) {
            clear();
        } else if (value.equals("⌫")) {
            backspace();
        } else if ("+-*/".contains(value)) {
            processOperator(value);
        } else if (value.equals("=")) {
            evaluateResult();
        }
    }

    private void processOperator(String nextOp) {
        String second = getSecondOperandString(display.getText());
        if (!pendingOperator.isEmpty() && !startNewInput && !second.isEmpty()) {
            calculateCurrent();
        } else if (pendingOperator.isEmpty()) {
            try { firstOperand = Double.parseDouble(display.getText()); } catch (Exception e) { firstOperand = 0; }
        }
        pendingOperator = nextOp;
        display.setText(formatResult(firstOperand) + " " + pendingOperator + " ");
        startNewInput = true;
    }

    private void evaluateResult() {
        if (pendingOperator.isEmpty()) return;
        String second = getSecondOperandString(display.getText());
        if (second.isEmpty()) return;
        calculateCurrent();
        pendingOperator = "";
        startNewInput = true;
    }

    private void calculateCurrent() {
        try {
            double secondOperand = Double.parseDouble(getSecondOperandString(display.getText()));
            firstOperand = calculate(firstOperand, secondOperand, pendingOperator);
            display.setText(formatResult(firstOperand));
        } catch (Exception e) {
            display.setText("Error");
        }
    }

    private void backspace() {
        String text = display.getText();
        String second = getSecondOperandString(text);
        if (!pendingOperator.isEmpty() && !second.isEmpty()) {
            display.setText(text.substring(0, text.length() - 1));
            if (second.length() == 1) startNewInput = true;
            return;
        }
        if (text.length() <= 1 || text.equals("Error")) clear();
        else display.setText(text.substring(0, text.length() - 1));
    }

    private void clear() {
        display.setText("0"); firstOperand = 0; pendingOperator = ""; startNewInput = true;
    }

    private String getSecondOperandString(String text) {
        String opText = " " + pendingOperator + " ";
        return text.contains(opText) ? text.substring(text.indexOf(opText) + opText.length()).trim() : "";
    }

    private String formatResult(double res) {
        return res == (long) res ? String.format("%d", (long) res) : String.valueOf(res);
    }

    private double calculate(double op1, double op2, String op) {
        return switch (op) {
            case "+" -> op1 + op2;
            case "-" -> op1 - op2;
            case "*" -> op1 * op2;
            case "/" -> {
                if (op2 == 0) display.setText("Error");
                yield op2 == 0 ? 0 : op1 / op2;
            }
            default -> op2;
        };
    }

    public static void main(String[] args) { launch(args); }
}

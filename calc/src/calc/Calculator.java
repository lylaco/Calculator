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
            {"C", "⌫", "/", "*"},
            {"7", "8", "9", "-"},
            {"4", "5", "6", "+"},
            {"1", "2", "3", "="},
            {"0"}
        };

        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        for (int row = 0; row < buttonLabels.length; row++) {
            for (int col = 0; col < buttonLabels[row].length; col++) {

                String label = buttonLabels[row][col];

                Button button = new Button(label);

                button.setPrefSize(60, 50);
                button.setMaxSize(
                    Double.MAX_VALUE,
                    Double.MAX_VALUE
                );

                button.setOnAction(
                    e -> handleButtonClick(label)
                );

                if (label.equals("=")) {
                    grid.add(button, col, row, 2, 1);
                } else {
                    grid.add(button, col, row);
                }
            }
        }

        VBox root = new VBox(
            10,
            display,
            grid
        );

        root.setPadding(new Insets(15));

        Scene scene = new Scene(
            root,
            320,
            430
        );

        primaryStage.setTitle("Calculator");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void handleButtonClick(String value) {

        if ("0123456789".contains(value)) {

            if (startNewInput) {

                if (!pendingOperator.isEmpty()) {

                    display.setText(
                        display.getText() + value
                    );

                } else {

                    display.setText(value);
                }

                startNewInput = false;

            } else {

                display.setText(
                    display.getText() + value
                );
            }

        } else if (value.equals("C")) {

            display.setText("0");
            firstOperand = 0;
            pendingOperator = "";
            startNewInput = true;

        } else if (value.equals("⌫")) {

            backspace();

        } else if ("+-*/".contains(value)) {

            String text = display.getText();

            if (!pendingOperator.isEmpty() && !startNewInput) {

                String second =
                    getSecondOperandString(text);

                if (!second.isEmpty()) {

                    try {

                        double secondOperand =
                            Double.parseDouble(second);

                        firstOperand =
                            calculate(
                                firstOperand,
                                secondOperand,
                                pendingOperator
                            );

                    } catch (NumberFormatException e) {

                        display.setText("Error");
                        return;
                    }
                }

            } else if (pendingOperator.isEmpty()) {

                try {

                    firstOperand =
                        Double.parseDouble(text);

                } catch (NumberFormatException e) {

                    firstOperand = 0;
                }
            }

            pendingOperator = value;

            display.setText(
                formatResult(firstOperand)
                + " "
                + pendingOperator
                + " "
            );

            startNewInput = true;

        } else if (value.equals("=")) {

            if (pendingOperator.isEmpty()) {
                return;
            }

            String second =
                getSecondOperandString(display.getText());

            if (second.isEmpty()) {
                return;
            }

            try {

                double secondOperand =
                    Double.parseDouble(second);

                double result =
                    calculate(
                        firstOperand,
                        secondOperand,
                        pendingOperator
                    );

                display.setText(
                    formatResult(result)
                );

                firstOperand = result;
                pendingOperator = "";
                startNewInput = true;

            } catch (NumberFormatException e) {

                display.setText("Error");
            }
        }
    }

    private void backspace() {

        String text = display.getText();

        if (!pendingOperator.isEmpty()) {

            String second =
                getSecondOperandString(text);

            if (!second.isEmpty()) {

                if (second.length() == 1) {

                    display.setText(
                        text.substring(
                            0,
                            text.length() - 1
                        )
                    );

                    startNewInput = true;

                } else {

                    display.setText(
                        text.substring(
                            0,
                            text.length() - 1
                        )
                    );
                }

                return;
            }
        }

        if (text.length() <= 1) {

            display.setText("0");
            startNewInput = true;

        } else {

            display.setText(
                text.substring(
                    0,
                    text.length() - 1
                )
            );
        }
    }

    private String getSecondOperandString(String text) {

        if (!pendingOperator.isEmpty()) {

            String operatorText =
                " " + pendingOperator + " ";

            if (text.contains(operatorText)) {

                return text.substring(
                    text.indexOf(operatorText)
                    + operatorText.length()
                ).trim();
            }
        }

        return "";
    }

    private String formatResult(double result) {

        if (result == (long) result) {

            return String.format(
                "%d",
                (long) result
            );
        }

        return String.valueOf(result);
    }

    private double calculate(
        double op1,
        double op2,
        String operator
    ) {

        return switch (operator) {

            case "+" -> op1 + op2;

            case "-" -> op1 - op2;

            case "*" -> op1 * op2;

            case "/" -> {

                if (op2 == 0) {

                    display.setText("Error");
                    yield 0;
                }

                yield op1 / op2;
            }

            default -> op2;
        };
    }

    public static void main(String[] args) {
        launch(args);
    }
}
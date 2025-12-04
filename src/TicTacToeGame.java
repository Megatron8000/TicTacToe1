import javax.swing.*; // Импорт библиотеки Swing для GUI
import java.awt.*; // Импорт классов для работы с графикой и компоновкой
import java.awt.event.ActionEvent; // События действия (клики)
import java.awt.event.ActionListener; // Обработчик событий клика
import java.util.Random; // Для генерации случайных чисел — ход компьютера

public class TicTacToeGame extends JFrame { // Основной класс игры, наследует окно JFrame
    private final JButton[][] buttons = new JButton[3][3]; // Кнопки для игрового поля 3x3
    private final char[][] board = new char[3][3]; // Логическая матрица состояния поля ('X', 'O' или пусто)

    private static final char HUMAN = 'X'; // Символ человека
    private static final char AI = 'O'; // Символ компьютера
    private static final char EMPTY = ' '; // Пустая клетка

    private char currentPlayer = HUMAN; // Текущий игрок, ходит сначала человек
    private final JLabel statusLabel = new JLabel("Ход игрока X"); // Метка для статуса игры вверху окна
    private final Random random = new Random(); // Генератор случайных чисел для ИИ

    public TicTacToeGame() { // Конструктор — создаёт окно и компоненты
        setTitle("Крестики-нолики: человек vs компьютер"); // Заголовок окна
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Завершение программы при закрытии окна
        setSize(400, 450); // Размер окна
        setLocationRelativeTo(null); // Центрирование окна на экране
        setLayout(new BorderLayout()); // Размещение компонентов по областям окна

        statusLabel.setHorizontalAlignment(SwingConstants.CENTER); // Выравнивание текста по центру
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18)); // Стиль и размер шрифта
        add(statusLabel, BorderLayout.NORTH); // Добавляем метку в верхнюю часть окна

        JPanel boardPanel = new JPanel(new GridLayout(3, 3)); // Панель с сеткой 3x3 для кнопок
        Font btnFont = new Font(Font.SANS_SERIF, Font.BOLD, 60); // Крупный шрифт для кнопок

        for (int row = 0; row < 3; row++) { // Для каждой строки
            for (int col = 0; col < 3; col++) { // Для каждого столбца
                buttons[row][col] = new JButton(""); // Создаем пустую кнопку
                buttons[row][col].setFont(btnFont); // Устанавливаем шрифт
                buttons[row][col].setFocusPainted(false); // Убираем стандартную рамку выделения
                final int r = row; // Константы для доступа из лямбды обработчика
                final int c = col;
                buttons[row][col].addActionListener(new ActionListener() { // Обработчик клика
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleHumanMove(r, c); // Вызываем метод обработки хода игрока
                    }
                });
                boardPanel.add(buttons[row][col]); // Добавляем кнопку в панель
                board[row][col] = EMPTY; // Инициализируем логическое поле пустыми символами
            }
        }

        add(boardPanel, BorderLayout.CENTER); // Добавляем панель с кнопками в центр окна

        JButton resetButton = new JButton("Новая игра"); // Кнопка перезапуска игры
        resetButton.addActionListener(e -> resetGame()); // Обработчик нажатия — сброс игры
        add(resetButton, BorderLayout.SOUTH); // Добавляем кнопку внизу окна

        setVisible(true); // Отобразить окно
    }

    // Обработка хода человека
    private void handleHumanMove(int row, int col) {
        if (currentPlayer != HUMAN) return; // Игрок ходит только своей очередью
        if (board[row][col] != EMPTY) return; // Клетка должна быть пустой

        makeMove(row, col, HUMAN); // Записать ход в поле и обновить кнопку
        if (checkEndOfGame(HUMAN)) return; // Проверить, закончилась ли игра

        currentPlayer = AI; // Передать ход компьютеру
        statusLabel.setText("Ход компьютера O"); // Обновить статус

        SwingUtilities.invokeLater(() -> { // Выполнить ход компьютера в очереди событий Swing
            makeAiMove(); // Сделать ход компьютера
            checkEndOfGame(AI); // Проверить, завершена ли игра
            if (!isBoardFull() && !checkWin(AI)) { // Если игра не закончена
                currentPlayer = HUMAN; // Вернуть ход человеку
                statusLabel.setText("Ход игрока X"); // Обновить статус
            }
        });
    }

    // Совершить ход — обновить логику и GUI
    private void makeMove(int row, int col, char player) {
        board[row][col] = player; // Логическое поле
        buttons[row][col].setText(String.valueOf(player)); // Отобразить символ игрока на кнопке
        buttons[row][col].setEnabled(false); // Отключить кнопку, чтобы её нельзя было повторно нажать
    }

    // Простая логика ИИ — ход случайной свободной клеткой
    private void makeAiMove() {
        if (isBoardFull() || checkWin(HUMAN) || checkWin(AI)) return; // Не ходить, если игра закончена

        int row, col;
        do {
            row = random.nextInt(3); // Случайный номер строки
            col = random.nextInt(3); // Случайный номер столбца
        } while (board[row][col] != EMPTY); // Пока не найдём пустую клетку

        makeMove(row, col, AI); // Совершить ход компьютера
    }

    // Проверка окончания игры: победа или ничья, с выводом результатов
    private boolean checkEndOfGame(char player) {
        if (checkWin(player)) { // Победа
            String msg = (player == HUMAN) ? "Вы выиграли!" : "Компьютер выиграл!";
            statusLabel.setText(msg); // Сообщить в окне
            disableAllButtons(); // Заблокировать поле
            JOptionPane.showMessageDialog(this, msg); // Всплывающее окно с результатом
            return true;
        } else if (isBoardFull()) { // Ничья
            statusLabel.setText("Ничья!");
            JOptionPane.showMessageDialog(this, "Ничья!");
            return true;
        }
        return false; // Игра продолжается
    }

    // Проверка победы — 3 символа подряд по строкам, столбцам и диагоналям
    private boolean checkWin(char player) {
        for (int row = 0; row < 3; row++) {
            if (board[row][0] == player && board[row][1] == player && board[row][2] == player) return true;
        }
        for (int col = 0; col < 3; col++) {
            if (board[0][col] == player && board[1][col] == player && board[2][col] == player) return true;
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) return true;
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) return true;
        return false;
    }

    // Проверка, заполнено ли всё поле, чтобы определить ничью
    private boolean isBoardFull() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == EMPTY) return false;
            }
        }
        return true;
    }

    // Отключить все кнопки после окончания игры
    private void disableAllButtons() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col].setEnabled(false);
            }
        }
    }

    // Сброс игрового поля и возвращение к началу новой игры
    private void resetGame() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                board[row][col] = EMPTY; // Очистить логику
                buttons[row][col].setText(""); // Очистить текст кнопок
                buttons[row][col].setEnabled(true); // Сделать кнопки активными
            }
        }
        currentPlayer = HUMAN; // Человек ходит первым
        statusLabel.setText("Ход игрока X"); // Сбросить статус
    }

    // Главный метод — точка входа, запускает графический интерфейс
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicTacToeGame::new); // Запуск GUI в правильном потоке Swing
    }
}
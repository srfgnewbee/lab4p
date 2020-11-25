package SaverchenkoGroup10Lab4VarC2;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class MainFrame extends JFrame {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;

    JMenuItem modify;
    JMenuItem modifyCondition;
    JMenuItem showGrid;
    JMenuItem turnLeft;
    JMenuItem showAxis;
    JMenuItem setToDefault;

    private JFileChooser fileChooser = null;
    private boolean fileLoaded = false;
    GraphicsDisplay display = new GraphicsDisplay();
    GraphicsMenuListener menu = new GraphicsMenuListener();

    public MainFrame() {

        super("Построение графиков функций на основе заранее подготовленных файлов");
        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH) / 2, (kit.getScreenSize().height - HEIGHT) / 2);
        Image img = kit.getImage("src/SaverchenkoGroup10Lab4VarC2/icon.PNG");
        setIconImage(img);
        setExtendedState(MAXIMIZED_BOTH);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu file = menuBar.add(new JMenu("Файл"));
        JMenu graphics = menuBar.add(new JMenu("График"));
        graphics.addMenuListener(new GraphicsMenuListener());
        JMenuItem open = file.add(new JMenuItem("Открыть файл с графиком"));
        open.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("C:\\Users\\SergeySaber\\IdeaProjects\\untitled"));
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("bin files", "bin");
                    fileChooser.setFileFilter(filter);
                }
                if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION)
                    openGraphics(fileChooser.getSelectedFile());
            }
        });
        JMenuItem close = file.add(new JMenuItem("Выход"));
        close.setAccelerator(KeyStroke.getKeyStroke("ctrl E"));
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        showAxis = graphics.add(new JCheckBoxMenuItem("Показать оси"));
        showAxis.setSelected(true);
        showAxis.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                display.setShowAxis(showAxis.isSelected());
            }
        });

        modify = graphics.add(new JCheckBoxMenuItem("Модификация отображения"));
        modify.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                display.setShowMarkers(modify.isSelected());
            }
        });

        modifyCondition = graphics.add(new JCheckBoxMenuItem("Модификация отображения с условием"));
        modifyCondition.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                display.setShowMarkersCondition(modifyCondition.isSelected());
            }
        });

        turnLeft = graphics.add(new JCheckBoxMenuItem("Поворот влево на 90°"));
        turnLeft.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });

        showGrid = graphics.add(new JCheckBoxMenuItem("Показать сетку"));
        showGrid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });

        setToDefault = graphics.add(new JMenuItem("Отменить все изменения"));
        setToDefault.setEnabled(false);
        setToDefault.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menu.menuGlobal(false);
                setToDefault.setEnabled(false);
            }
        });
        getContentPane().add(display, BorderLayout.CENTER);

    }

    public void menuStatus (){

        setToDefault.setEnabled(menu.atLeastOne());
        if (modify.isSelected())
            modifyCondition.setEnabled(true);
        else {
            modifyCondition.setSelected(false);
            modifyCondition.setEnabled(false);
            display.setShowMarkersCondition(false);
        }
    }

    protected void openGraphics (File selectedFile) {
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(selectedFile));
            Double[][] graphicsData = new Double[in.available() / (Double.SIZE / 8) / 4][];
            int i = 0;
            while (in.available() > 0) {
                double x = in.readDouble();
                double y = in.readDouble();
                in.skipBytes(2*(Double.SIZE/8));
                graphicsData[i++] = new Double[]{x, y};
            }

            if (graphicsData.length > 0) {
                fileLoaded = true;
                menu.menuGlobal(false);
                display.showGraphics(graphicsData);
            }

            in.close();

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(MainFrame.this, "Указанный файл не найден",
                    "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(MainFrame.this, "Ошибка чтения координат точек из файла",
                    "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);
        while(frame.isVisible())
            frame.menuStatus();
    }

    private class GraphicsMenuListener implements MenuListener {

        public void menuSelected(MenuEvent e) {
            turnLeft.setEnabled(fileLoaded);
            modify.setEnabled(fileLoaded);
            modifyCondition.setEnabled(false);
            showGrid.setEnabled(fileLoaded);
        }

        public void menuGlobal(boolean off){
            turnLeft.setSelected(off);
            modify.setSelected(off);
            display.setShowMarkers(off);
            modifyCondition.setSelected(off);
            showGrid.setSelected(off);
            display.setShowAxis(!off);
            showAxis.setSelected(!off);
        }

        public boolean atLeastOne(){
            if (turnLeft.isSelected() || modify.isSelected())
                return true;
            else if (!showAxis.isSelected())
                return true;
            else return modifyCondition.isSelected() || showGrid.isSelected();
        }

        public void menuDeselected(MenuEvent e) {}

        public void menuCanceled(MenuEvent e) {}
    }
}
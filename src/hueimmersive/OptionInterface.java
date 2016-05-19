package hueimmersive;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import hueimmersive.interfaces.ILight;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;


public class OptionInterface {
    private JFrame frame;
    private JCheckBox checkbox_AutoTurnOff;
    private JLabel label_ThresholdPercentage;
    private JSlider slider_Threshold;
    private JLabel label_BrightnessThreshold;
    private JCheckBox checkbox_UseGammaCorrection;
    private JPanel panel_Lights;
    private JComboBox checkbox_Screen;
    private JCheckBox checkbox_ForceOn;
    private JCheckBox checkbox_ForceStart;
    private JCheckBox checkbox_ForceOff;
    private JCheckBox checkbox_Log;
    private JPanel panel_Threshold;

    public OptionInterface()
    {
        Main.ui.setEnabled(false);
        initialize();
        getOptions();
    }

    private void initialize()
    {
        frame = new JFrame();
        frame.setMinimumSize(new Dimension(460, 500));
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0)
            {
                Settings.set("oi_x", frame.getX());
                Settings.set("oi_y", frame.getY());
                Main.ui.setEnabled(true);
            }
        });
        frame.setTitle("options");
        frame.setBounds(100, 100, 460, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.setLocation(Settings.getInteger("oi_x"), Settings.getInteger("oi_y"));
        frame.getContentPane().setLayout(new FormLayout(new ColumnSpec[]{
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("left:20dlu:grow"),
                ColumnSpec.decode("left:20dlu:grow"),
                ColumnSpec.decode("left:20dlu:grow"),
                FormSpecs.RELATED_GAP_COLSPEC,},
                new RowSpec[]{
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("16dlu"),
                        FormSpecs.LINE_GAP_ROWSPEC,
                        RowSpec.decode("16dlu:grow"),
                        FormSpecs.LINE_GAP_ROWSPEC,
                        RowSpec.decode("16dlu"),
                        RowSpec.decode("10dlu"),
                        RowSpec.decode("16dlu"),
                        RowSpec.decode("10dlu"),
                        RowSpec.decode("162dlu"),
                        RowSpec.decode("10dlu"),
                        RowSpec.decode("16dlu"),
                        FormSpecs.LINE_GAP_ROWSPEC,
                        RowSpec.decode("16dlu"),
                        RowSpec.decode("10dlu"),
                        RowSpec.decode("bottom:16dlu"),
                        FormSpecs.RELATED_GAP_ROWSPEC,}));

        JLabel label_LightOptions = new JLabel("light options:");
        label_LightOptions.setEnabled(false);
        label_LightOptions.setFont(new Font("Tahoma", Font.PLAIN, 14));
        frame.getContentPane().add(label_LightOptions, "2, 2, 1, 5, center, default");

        checkbox_AutoTurnOff = new JCheckBox(" auto. turn off lights (experimental v3)");
        checkbox_AutoTurnOff.setToolTipText("turns the lights automatically off when the screen is near black");
        checkbox_AutoTurnOff.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                for (Component component : panel_Threshold.getComponents())
                {
                    component.setEnabled(checkbox_AutoTurnOff.isSelected());
                }
            }
        });
        frame.getContentPane().add(checkbox_AutoTurnOff, "3, 2, 2, 1, left, center");

        panel_Threshold = new JPanel();
        frame.getContentPane().add(panel_Threshold, "3, 3, 2, 2, fill, top");
        panel_Threshold.setLayout(new FormLayout(new ColumnSpec[]{
                ColumnSpec.decode("16dlu"),
                ColumnSpec.decode("17dlu"),
                ColumnSpec.decode("95dlu"),
                ColumnSpec.decode("default:grow"),},
                new RowSpec[]{
                        RowSpec.decode("default:grow"),}));

        label_ThresholdPercentage = new JLabel("10 %");
        panel_Threshold.add(label_ThresholdPercentage, "2, 1, right, default");

        slider_Threshold = new JSlider();
        slider_Threshold.setPaintTicks(true);
        slider_Threshold.setMinorTickSpacing(1);
        slider_Threshold.setMajorTickSpacing(2);
        slider_Threshold.setValue(10);
        slider_Threshold.setSnapToTicks(true);
        slider_Threshold.setMaximum(30);
        slider_Threshold.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e)
            {
                label_ThresholdPercentage.setText(slider_Threshold.getValue() + " %");
                ;
            }
        });
        panel_Threshold.add(slider_Threshold, "3, 1");

        label_BrightnessThreshold = new JLabel(" brightness threshold");
        panel_Threshold.add(label_BrightnessThreshold, "4, 1, left, default");

        checkbox_UseGammaCorrection = new JCheckBox(" use gamma correction");
        checkbox_UseGammaCorrection.setToolTipText("makes the color more like the color on your screen");
        frame.getContentPane().add(checkbox_UseGammaCorrection, "3, 6, 2, 1, left, center");

        JSeparator separator_1 = new JSeparator();
        frame.getContentPane().add(separator_1, "2, 7, 3, 1, fill, center");

        JButton button_Ok = new JButton("ok");
        button_Ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                saveOptions();
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                frame.dispose();
            }
        });

        JLabel label_ScreenOptions = new JLabel("capture options:");
        label_ScreenOptions.setFont(new Font("Tahoma", Font.PLAIN, 14));
        label_ScreenOptions.setEnabled(false);
        frame.getContentPane().add(label_ScreenOptions, "2, 8, center, default");

        JLabel label_CaptureScreen = new JLabel("    capture screen");
        label_CaptureScreen.setToolTipText("select the screen to capture");
        frame.getContentPane().add(label_CaptureScreen, "4, 8, left, center");

        JSeparator separator_3 = new JSeparator();
        frame.getContentPane().add(separator_3, "2, 11, 3, 1, fill, center");

        JLabel label_StartupOptions = new JLabel("startup options:");
        label_StartupOptions.setEnabled(false);
        label_StartupOptions.setFont(new Font("Tahoma", Font.PLAIN, 14));
        frame.getContentPane().add(label_StartupOptions, "2, 12, 1, 3, center, center");

        checkbox_ForceOn = new JCheckBox("   force on");
        checkbox_ForceOn.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e)
            {
                updateSelectedArguments();
            }
        });
        checkbox_ForceOn.setToolTipText("turn lights on at startup");
        frame.getContentPane().add(checkbox_ForceOn, "3, 12, left, center");

        checkbox_ForceStart = new JCheckBox("   force start");
        checkbox_ForceStart.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e)
            {
                updateSelectedArguments();
            }
        });
        checkbox_ForceStart.setToolTipText("start the immersive lighting at startup");
        frame.getContentPane().add(checkbox_ForceStart, "4, 12, left, center");

        checkbox_ForceOff = new JCheckBox("   force off");
        checkbox_ForceOff.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e)
            {
                updateSelectedArguments();
            }
        });
        checkbox_ForceOff.setToolTipText("turn lights off at startup");
        frame.getContentPane().add(checkbox_ForceOff, "3, 14, left, center");

        checkbox_Log = new JCheckBox("   log");
        checkbox_Log.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e)
            {
                updateSelectedArguments();
            }
        });
        checkbox_Log.setToolTipText("create a log");
        frame.getContentPane().add(checkbox_Log, "4, 14, left, center");

        JSeparator separator = new JSeparator();
        frame.getContentPane().add(separator, "2, 15, 3, 1, fill, center");
        frame.getContentPane().add(button_Ok, "2, 16, fill, fill");

        ArrayList<String> screens = new ArrayList<String>();
        for (int i = 0; i < GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length; i++)
        {
            screens.add("Display " + (i + 1));
        }
        checkbox_Screen = new JComboBox();
        checkbox_Screen.setToolTipText("select the screen to capture");
        checkbox_Screen.setModel(new DefaultComboBoxModel(screens.toArray()));
        ((JLabel) checkbox_Screen.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
        if (screens.size() <= 1)
        {
            checkbox_Screen.setEnabled(false);
        }
        frame.getContentPane().add(checkbox_Screen, "3, 8, fill, center");

        JButton button_Cancel = new JButton("cancel");
        button_Cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                frame.dispose();
            }
        });
        frame.getContentPane().add(button_Cancel, "3, 16, fill, fill");

        JButton button_Apply = new JButton("apply");
        button_Apply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0)
            {
                saveOptions();
            }
        });
        frame.getContentPane().add(button_Apply, "4, 16, fill, fill");

        JSeparator separator_2 = new JSeparator();
        frame.getContentPane().add(separator_2, "2, 9, 3, 1, fill, center");

        JScrollPane scrollpane = new JScrollPane();
        scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        frame.getContentPane().add(scrollpane, "2, 10, 3, 1, fill, fill");


        // setup list with options for all lights

        panel_Lights = new JPanel();
        scrollpane.setViewportView(panel_Lights);
        int rows = Control.bridge.getLights().size();
        if (rows < 6)
        {
            rows = 6;
        }
        panel_Lights.setLayout(new GridLayout(rows, 1, 5, 7));

        JLabel lblActiveNameColor = new JLabel("   active         name                               color algorithm                   brightness\r\n");
        scrollpane.setColumnHeaderView(lblActiveNameColor);

        // create the list
        for (final ILight light : Control.bridge.getLights())
        {
            final JPanel panel_options = new JPanel();
            panel_Lights.add(panel_options, Control.bridge.getLights().indexOf(light));

            JLabel label_Name = new JLabel(light.getName());
            label_Name.setPreferredSize(new Dimension(110, 15));

            final JList list_Algorithms = new JList();
            list_Algorithms.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            list_Algorithms.setVisibleRowCount(1);
            list_Algorithms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list_Algorithms.setModel(new AbstractListModel() {
                String[] values = new String[]{"   A   ", "   B   ", "   C   ", "   D   "};

                public int getSize()
                {
                    return values.length;
                }

                public Object getElementAt(int index)
                {
                    return values[index];
                }
            });
            list_Algorithms.setSelectedIndex(Settings.Light.getAlgorithm(light));
            list_Algorithms.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent arg0)
                {
                    int algorithm = list_Algorithms.locationToIndex(arg0.getPoint());
                    if (algorithm == 0)
                    {
                        list_Algorithms.setToolTipText("average color");
                    } else if (algorithm == 1)
                    {
                        list_Algorithms.setToolTipText("saturated color");
                    } else if (algorithm == 2)
                    {
                        list_Algorithms.setToolTipText("bright color");
                    } else if (algorithm == 3)
                    {
                        list_Algorithms.setToolTipText("dark color");
                    }
                }
            });

            JPanel panel_Brightness = new JPanel();
            panel_Brightness.setLayout(new FormLayout(new ColumnSpec[]{
                    ColumnSpec.decode("fill:93px"),
                    ColumnSpec.decode("right:29px:grow"),},
                    new RowSpec[]{
                            RowSpec.decode("24px"),}));

            final JSlider slider_Brightness = new JSlider();
            slider_Brightness.setSnapToTicks(true);
            slider_Brightness.setMinorTickSpacing(5);
            slider_Brightness.setMinimum(10);
            slider_Brightness.setMaximum(100);
            slider_Brightness.setValue(Settings.Light.getBrightness(light));
            panel_Brightness.add(slider_Brightness, "1, 1, center, center");

            final JLabel label_Brightness = new JLabel("100%");
            label_Brightness.setText(slider_Brightness.getValue() + "%");
            label_Brightness.setFont(new Font("Tahoma", Font.PLAIN, 10));
            panel_Brightness.add(label_Brightness, "2, 1, right, center");
            slider_Brightness.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent arg0)
                {
                    label_Brightness.setText(slider_Brightness.getValue() + "%");
                }
            });

            final JCheckBox checkbox_Active = new JCheckBox();
            checkbox_Active.setSelected(Settings.Light.getActive(light));
            checkbox_Active.setToolTipText("allow the program to change this lights color and brightness");
            if (checkbox_Active.isSelected() == false)
            {
                label_Name.setEnabled(false);
                list_Algorithms.setEnabled(false);
                slider_Brightness.setEnabled(false);
                label_Brightness.setEnabled(false);
            }
            checkbox_Active.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0)
                {
                    try
                    {
                        if (checkbox_Active.isSelected())
                        {
                            panel_options.getComponent(1).setEnabled(true);
                            panel_options.getComponent(2).setEnabled(true);
                            JPanel panel = (JPanel) panel_options.getComponent(3);
                            panel.getComponent(0).setEnabled(true);
                            panel.getComponent(1).setEnabled(true);
                        } else
                        {
                            panel_options.getComponent(1).setEnabled(false);
                            panel_options.getComponent(2).setEnabled(false);
                            panel_options.getComponent(3).setEnabled(false);
                            JPanel panel = (JPanel) panel_options.getComponent(3);
                            panel.getComponent(0).setEnabled(false);
                            panel.getComponent(1).setEnabled(false);
                        }
                    } catch (Exception e)
                    {
                        Debug.exception(e);
                    }
                }
            });

            FlowLayout flowlayout_options = new FlowLayout(FlowLayout.LEFT, 12, 4);
            panel_options.setLayout(flowlayout_options);
            panel_options.add(checkbox_Active, 0);
            panel_options.add(label_Name, 1);
            panel_options.add(list_Algorithms, 2);
            panel_options.add(panel_Brightness, 3);
        }

        frame.pack();
        frame.setVisible(true);
    }

    private void getOptions() // get saved options and setup window elements
    {
        checkbox_AutoTurnOff.setSelected(Settings.getBoolean("autoswitch"));
        slider_Threshold.setValue(Settings.getInteger("autoswitchthreshold"));
        for (Component component : panel_Threshold.getComponents())
        {
            component.setEnabled(Settings.getBoolean("autoswitch"));
        }

        checkbox_UseGammaCorrection.setSelected(Settings.getBoolean("gammacorrection"));
        checkbox_Screen.setSelectedIndex(Settings.getInteger("screen"));

        for (String arg : Settings.getArguments())
        {
            switch (arg)
            {
                case "force-on":
                    checkbox_ForceOn.setSelected(true);
                    checkbox_ForceOff.setSelected(false);
                    break;
                case "force-off":
                    checkbox_ForceOff.setSelected(true);
                    checkbox_ForceOn.setSelected(false);
                    checkbox_ForceStart.setSelected(false);
                    break;
                case "force-start":
                    checkbox_ForceStart.setSelected(true);
                    checkbox_ForceOff.setSelected(false);
                    break;
                case "log":
                    checkbox_Log.setSelected(true);
                    break;
            }
        }

        updateSelectedArguments();
    }

    private void updateSelectedArguments()
    {
        checkbox_ForceOn.setEnabled(!checkbox_ForceOff.isSelected());
        checkbox_ForceOff.setEnabled(!checkbox_ForceOn.isSelected() && !checkbox_ForceStart.isSelected());
        checkbox_ForceStart.setEnabled(!checkbox_ForceOff.isSelected());
        checkbox_Log.setEnabled(true);
    }

    private void saveOptions() // save all settings
    {
        Settings.set("autoswitch", checkbox_AutoTurnOff.isSelected());
        Settings.set("autoswitchthreshold", slider_Threshold.getValue());
        Settings.set("gammacorrection", checkbox_UseGammaCorrection.isSelected());

        Settings.set("screen", checkbox_Screen.getSelectedIndex());

        for (ILight light : Control.bridge.getLights())
        {
            JPanel panel_Light = (JPanel) panel_Lights.getComponent(Control.bridge.getLights().indexOf(light));

            JCheckBox checkbox_Active = (JCheckBox) panel_Light.getComponent(0);
            Settings.Light.setActive(light, checkbox_Active.isSelected());

            JList list_Algorithms = (JList) panel_Light.getComponent(2);
            Settings.Light.setAlgorithm(light, list_Algorithms.getSelectedIndex());

            JPanel panel_Brightness = (JPanel) panel_Light.getComponent(3);
            JSlider slider_Brightness = (JSlider) panel_Brightness.getComponent(0);
            Settings.Light.setBrightness(light, slider_Brightness.getValue());
        }

        ArrayList<String> args = new ArrayList<String>();
        if (checkbox_ForceOn.isSelected())
        {
            args.add("force-on");
        }
        if (checkbox_ForceOff.isSelected())
        {
            args.add("force-off");
        }
        if (checkbox_ForceStart.isSelected())
        {
            args.add("force-start");
        }
        if (checkbox_Log.isSelected())
        {
            args.add("log");
        }
        Settings.setArguments(args);

        try
        {
            Main.ui.setupOnOffButton();
        } catch (Exception e)
        {
            Debug.exception(e);
        }
    }
}

package ru.sscs.client.components;

import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;


public class TextField extends JTextField
{
	private static final long serialVersionUID = 1L;

	public TextField(String text) {
            setOpaque(false);
            setCaretColor(Color.DARK_GRAY);
            setForeground(Color.DARK_GRAY);
            setText(text);
            setBorder(new EmptyBorder(0, 10, 0, 10));
	}
}
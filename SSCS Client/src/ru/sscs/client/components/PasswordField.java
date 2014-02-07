package ru.sscs.client.components;;
import java.awt.Color;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

public class PasswordField extends JPasswordField
{
	private static final long serialVersionUID = 1L;

	public PasswordField(String text) {
            setText(text);
            setCaretColor(Color.DARK_GRAY);
            setForeground(Color.DARK_GRAY);
            setOpaque(false);
            setBorder(new EmptyBorder(0, 10, 0, 10));
	}
}
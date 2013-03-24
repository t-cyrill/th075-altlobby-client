package jp.dip.th075altlobby.imo.Component;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

public class JWheelableComboBox extends JComboBox implements FocusListener, MouseWheelListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean focused = false;
	
	public JWheelableComboBox(DefaultComboBoxModel Model) {
		super(Model);
	}

	public JWheelableComboBox() {
		super();
	}

	/**
	 * 初期化を行います。
	 * 明示的に呼び出す必要があります。
	 */
	public void listener_init(){
		addMouseWheelListener(this);
		addFocusListener(this);
	}
	
	@Override
	public void focusGained(FocusEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		focused = true;
	}
	
	@Override
	public void focusLost(FocusEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		focused = false;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(!focused)
			return ;
		int move = e.getWheelRotation();
		int new_index = getSelectedIndex() + move;
		if(new_index < 0)
			new_index = 0;
		else if(new_index >= getItemCount())
			new_index = getItemCount() - 1;
		
		setSelectedIndex(new_index);
	}
}

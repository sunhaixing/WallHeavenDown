package com.sunhx.exam;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

class NotifyDialog extends JFrame {
	private static final long serialVersionUID = -1158010833749782259L;

	private int i = 0;
	private final Timer tmr;
	private int x, y;
	
	public NotifyDialog() {
		setTitle("Download ...");
		Dimension dim = App.getScreenSize();
		setBounds(dim.width - 201, 1, 200, 100);
		setAlwaysOnTop(true);
		setUndecorated(true);
		setOpacity(0.8f);
		setType(JFrame.Type.UTILITY);
		
		JLabel lblOut = new JLabel("0", JLabel.CENTER);
		lblOut.setBackground(new Color(0xFF, 0xD3, 0x1D));
		lblOut.setBorder(BorderFactory.createLineBorder(new Color(0xF5, 0x7B, 0x51), 1));
		lblOut.setForeground(new Color(0xD6, 0x34, 0x47));
		lblOut.setOpaque(true);
		add(lblOut, BorderLayout.CENTER);
		lblOut.setFont(new Font("Brush Script MT", Font.PLAIN, 40));
		
		lblOut.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				NotifyDialog.this.setLocation(
						NotifyDialog.this.getLocation().x + e.getX() - x, 
						NotifyDialog.this.getLocation().y + e.getY() - y);
			}
		});
		
		lblOut.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				x = e.getX();
				y = e.getY();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
			}
		});
		tmr = new Timer(1000, e -> {
			i++;
			lblOut.setText("" + i);
		});
	}
	
	public void Show() {
		i = 0;
		setVisible(true);
		tmr.start();
	}
	
	public void Hide() {
		tmr.stop();
		System.exit(0);
	}
}

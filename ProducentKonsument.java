package zadanie;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/* 
 *  Problem producenta i konsumenta
 *  Autor: Julia Zajusz
 *   Data: 17 grudnia 2016 r.
 */

class Producent extends Thread{
	static char item = 'A';	
	Bufor buf;
	int number;
	
	public Producent(Bufor c, int number){ 
		buf = c;
		this.number = number;
	}
	
	public void run(){ 
		char c;
		while(true){
			c = item++;
			buf.put(number, c);
			try {
				sleep((int)(Math.random() * 1000));				
				} catch (InterruptedException e) { }
		}
	}
}

class Konsument extends Thread{
	Bufor buf;
    int number;
    
	public Konsument(Bufor c, int number){ 
		buf = c;
		this.number = number;
	}
	
	public void run(){ 
		while(true){ 
			buf.get(number);
			try {
				sleep((int)(Math.random() * 1000));
				} catch (InterruptedException e) { }
		}
	}
}

class Bufor{
	private char contents;
	boolean stop = false;
	private int available=0;
	int x;
	JTextArea tekst;

	Bufor(JTextArea tArea, int x){
		tekst=tArea;
		this.x=x;	
	}
	public synchronized int get(int kons){
		while(stop==true){
			try{
			wait();
			}catch (InterruptedException e){}
		}
		tekst.insert("Konsument #" + kons + " chce zabrac\n",0);
		while (available <= 0){
			try { tekst.insert("Konsument #" + kons + "   bufor pusty - czekam\n",0);
			
				  wait();
				} catch (InterruptedException e) { }
		}
		available--;
		tekst.insert("Konsument #" + kons + "      zabral: " + contents+"\n",0);
		
		notifyAll();
		return contents;
	}

	public synchronized void put(int prod, char value){
		while(stop==true){
			try{
			wait();
			}catch (InterruptedException e){}
		}
		tekst.insert("Producent #" + prod + "  chce oddac: " + value+"\n",0);
		
		while (available>=x){
			try { tekst.insert("Producent #" + prod + "   bufor zajety - czekam\n",0);
			
				  wait();
				} catch (InterruptedException e) { }
		}
		contents = value;
		available ++;
		tekst.insert("Producent #" + prod + "       oddal: " + value+"\n",0);
		
		notifyAll();
	}
}


public class ProducentKonsument extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;

	public List<Producent> producenci=new ArrayList<Producent>();
	public List<Konsument> konsumenci=new ArrayList<Konsument>();

	   Integer[] l={1,2,3,4,5};
	   JComboBox listaB;
	   JComboBox listaP;
	   JComboBox listaK;
	   JTextArea tArea;
	   
	   Container cp = this.getContentPane();
	   Container cp2=new Container();
	   
	JButton buttonStart;
	JButton buttonWstrzymaj;
	JButton buttonWznow;
	Bufor c;
	int x=1,y=1,z=1;
	
	ProducentKonsument(){
		super("Problem producenta i konsumenta");
		setSize(600,500);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JPanel tfPanel = new JPanel();
		JPanel areaPanel = new JPanel();
		
		
	      tfPanel.setBorder(BorderFactory.createTitledBorder("Ustawienia: "));
	 
	      tfPanel.add(new JLabel("  Rozmiar bufora: "));
	      listaB=new JComboBox(l);
	      listaB.addActionListener(this);
	      tfPanel.add(listaB);
	      
	      tfPanel.add(new JLabel("  Ilosc producentow: "));
	      listaP = new JComboBox(l);
	      listaP.addActionListener(this);
	      tfPanel.add(listaP);

	      tfPanel.add(new JLabel("  Ilosc konsumentow: "));
	      listaK = new JComboBox(l);
	      listaK.addActionListener(this);
	      tfPanel.add(listaK);
	 
	      
	      tArea = new JTextArea();
	     // tArea.setSize(300, 200);
	      tArea.setFont(new Font("Sans", Font.ROMAN_BASELINE, 12));
	      tArea.setBackground(Color.WHITE);
	     
	      JScrollPane tAreaScrollPane = new JScrollPane(tArea);
	      tAreaScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 100));
	      tAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	      tAreaScrollPane.setSize(250, 100);
	      
	     
	      buttonStart = new JButton("Start");
	      buttonWstrzymaj = new JButton("Wstrzymaj");
	      buttonWznow = new JButton("Wznow");
	      
			buttonWstrzymaj.setEnabled(false);
			buttonWznow.setEnabled(false);
	     // areaPanel.add(tArea);//
	     // areaPanel.add(tAreaScrollPane);//
	      areaPanel.add(buttonStart);
	      areaPanel.add(buttonWstrzymaj);
	      areaPanel.add(buttonWznow);
	      buttonStart.addActionListener(this);
	      buttonWstrzymaj.addActionListener(this);
	      buttonWznow.addActionListener(this);
	      
	      cp.setLayout(new GridBagLayout());
	      GridBagConstraints c = new GridBagConstraints();
	      c.fill = GridBagConstraints.HORIZONTAL;
	      c.weightx=1.0;
	      c.weighty=1.0;
	      c.ipady=20;
	      c.gridx=0;
	      c.gridy=0;
	      cp.add(tfPanel, c);
	      
	      c.fill = GridBagConstraints.HORIZONTAL;
	      c.weightx=1.0;
	      c.ipady=100;
	      c.gridx=0;
	      c.gridy=1;
	      cp.add(tAreaScrollPane, c);
	      
	      c.fill = GridBagConstraints.HORIZONTAL;
	      c.weightx=1.0;
	      c.ipady=20;
	      c.gridx=0;
	      c.gridy=2;
	      cp.add(areaPanel, c);

	      
		setVisible(true);
	}

	void producentKonsument(){
		try { Thread.sleep( 5000 );
			} catch (InterruptedException e) { }
		System.exit(0);
	}
	
	public static void main(String[] args){
		ProducentKonsument a=new ProducentKonsument();		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object zrodlo = arg0.getSource();
		
		
		if(zrodlo==listaB){
			JComboBox a=(JComboBox)zrodlo;
			x=a.getSelectedIndex();
			x++;
		}
		
		if(zrodlo==listaP){
			JComboBox a=(JComboBox)zrodlo;
			y=a.getSelectedIndex();
			y++;
		}
		
		if(zrodlo==listaK){
			JComboBox a=(JComboBox)zrodlo;
			z=a.getSelectedIndex();
			z++;
		}
		if(zrodlo==buttonStart){
			
			listaB.setEnabled(false);
			listaP.setEnabled(false);
			listaK.setEnabled(false);
			buttonStart.setEnabled(false);
			buttonWstrzymaj.setEnabled(true);
			buttonWznow.setEnabled(true);
			
			c=new Bufor(tArea,x);
			for(int i=0;i<y;i++){
				producenci.add(new Producent(c, i+1));
				producenci.get(i).start();
			}
			for(int i=0;i<z;i++){
				konsumenci.add(new Konsument(c, i+1));
				konsumenci.get(i).start();
			}
		}
		
		if(zrodlo==buttonWstrzymaj){
			synchronized(c){
			c.stop=true;
			}
		}
		if(zrodlo==buttonWznow){
			synchronized(c){
			c.stop=false;
			c.notifyAll();
			}
		}

	}
}




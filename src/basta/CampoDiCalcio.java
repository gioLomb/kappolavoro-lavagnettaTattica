package basta;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;


public class CampoDiCalcio extends JFrame {
	private static double economia = 0;
	private static final int LARGHEZZA_CAMPO = 727;//600
	private static final int ALTEZZA_CAMPO = 408;//400
	private static final int NUMERO_GIOCATORI = 11;
	private static Vector<CerchioGiocatore> giocatori = new Vector<>();
	private static Vector<CerchioGiocatore> panchinari = new Vector<>();
	private static String[] ruoli = { "POR", "DIF", "CEN", "ATT" };
	private static CampoDiCalcio campo = new CampoDiCalcio();
	private static JButton bench,rosaButton,saleButton,signingButton,budget,mod;

    public void caricaBudget() {
        BufferedReader br = null;
       
        
        try {
            FileReader fr = new FileReader("budget.txt");
            br = new BufferedReader(fr);
            economia =Double.parseDouble( br.readLine()); // Leggi la prima riga del file
        } catch (FileNotFoundException e) {
            
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
       
    }
public void salvaBudget() {
PrintWriter pw = null;
    
    try {
        FileWriter fw = new FileWriter("budget.txt");
        pw = new PrintWriter(fw);
        pw.write(economia+"");
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if (pw != null) {
            pw.close();
        }
    }
	
}
	public void carica(String path,Vector<CerchioGiocatore> vec) {
		
		  BufferedReader reader = null;
	        try {
	            reader = new BufferedReader(new FileReader(path));
	            String line;
	            while ((line = reader.readLine()) != null) {
	                String[] parts = line.split(";");
	                int x = Integer.parseInt(parts[0]);
	                int y = Integer.parseInt(parts[1]);
	                int numero = Integer.parseInt(parts[2]);
	                String nome = parts[3];
	                String cognome = parts[4];
	                String ruolo = parts[5];
	                double costo = Double.parseDouble(parts[6]);
	             
	                CerchioGiocatore nuovo = new CerchioGiocatore(x, y, numero, nome, cognome, ruolo, costo);
	                vec.add(nuovo);
	                repaint(); // Assuming there's a repaint method in this class.
	            }
	        } catch (FileNotFoundException fnf) {
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
		

	}

	 public void salva(String path,Vector<CerchioGiocatore> vec) {
		 BufferedWriter writer = null;
	        try {
	            writer = new BufferedWriter(new FileWriter(path));
	            for (CerchioGiocatore g : vec) {
	                writer.write(g.getX() + ";" + g.getY() + ";" + g.getNumero() + ";" + g.getNome() + ";" + g.getCognome() + ";" + g.getRuolo() + ";" + g.getCosto());
	                writer.newLine();
	            }
	            writer.flush();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (writer != null) {
	                try {
	                    writer.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }
	  

	public CampoDiCalcio() {
	caricaBudget();
carica("titolari.txt",giocatori);
carica("panchinari.txt",panchinari);
	
		try {
setResizable(false);
		setTitle("Campo di Calcio");
		setSize(LARGHEZZA_CAMPO, ALTEZZA_CAMPO+150);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(JOptionPane.showConfirmDialog(campo, "VUOI DAVVERO CHIUDERE?", "CHIUDI", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
					salva("titolari.txt",giocatori);
					salva("panchinari.txt",panchinari);
					salvaBudget();
					System.exit(0);
				}
			}
		});
		
		getContentPane().setLayout(null);

		addMouseListener(new MouseAdapter() {
			boolean active = false;
			JPopupMenu menu = null;// con un if e boolean controlla l'uscita del menu ad ogni tocco destro

			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					if (!active) {
						for (CerchioGiocatore giocatore : giocatori) {
							if (giocatore.contienePunto(e.getX(), e.getY())) {
								active = true;
								menu = new JPopupMenu();
								JMenuItem panchina = new JMenuItem("PANCHINA");
								panchina.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										active = false;
										menu.setVisible(false);
										panchinari.add(giocatore);
										giocatori.remove(giocatore);
										//scaricaTitolari();
										repaint();
										// JOptionPane.showMessageDialog(getContentPane(), panchinari.get(0).getNome());
										// menu.setVisible(false);
									}
								});
								JMenuItem m = new JMenuItem("SCHEDA GIOCATORE");

								m.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent evt) {
										active = false;
										menu.setVisible(false);
										// ripetizione con inputPanchina
										sheetLayout(giocatore);
									}

								});
								JMenuItem cessione = new JMenuItem("CEDI");
								cessione.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent evt) {
										menu.setVisible(false);
										sellByBenchPitch(giocatore, giocatori);
									}
								});
								menu.add(cessione);
								menu.add(m);
								menu.add(panchina);
								menu.show(null, e.getX(), e.getY());

								break;
							}
						}
					} else {
						menu.setVisible(false);
						active = false;
					}
				} else {

					for (CerchioGiocatore giocatore : giocatori) {

						if (giocatore.contienePunto(e.getX(), e.getY())) {
							System.out.print(e.getX()+"---"+e.getY());
							giocatore.setTrascinando(true);
							break;
						}
					}
					active = false;
					menu.setVisible(false);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				for (CerchioGiocatore giocatore : giocatori) {
					
					giocatore.setTrascinando(false);
				}
			}
		});

		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				for (CerchioGiocatore giocatore : giocatori) {
					if (giocatore.isTrascinando() && e.getY() < ALTEZZA_CAMPO - CerchioGiocatore.raggio
							&& e.getX() < LARGHEZZA_CAMPO - CerchioGiocatore.raggio
							&& e.getX() > CerchioGiocatore.raggio && e.getY() > CerchioGiocatore.raggio + 20
							&& controlloSovrapposizione(e.getX(), e.getY())) {// CONTROLLA SOVRAPPOSIZIONE
						giocatore.setPosizione(e.getX(), e.getY());

						repaint();

						// break;
					}
				}
			}
		});
	}catch(Exception e) {
		
	}}


	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.GREEN);
		g.fillRect(0, 0, 	LARGHEZZA_CAMPO, 500); // LARGHEZZA_CAMPO = 700, ALTEZZA_CAMPO = 500

		g.setColor(Color.WHITE);
		g.drawRect(0, 0, LARGHEZZA_CAMPO, 500);
		g.drawLine(LARGHEZZA_CAMPO / 2, 500, LARGHEZZA_CAMPO / 2, 0); // Linea di centro campo
		g.drawOval(313, 200, 100, 100); // Cerchio di centro campo
		g.drawRect(677, 215, 50, 100); // Area di rigore destra
		g.drawRect(0, 215, 50, 100); // Area di rigore sinistra


		g.setColor(Color.BLUE);
		for (CerchioGiocatore giocatore : giocatori) {
			giocatore.draw(g);
		}

		
	}
public int posAboutRole(String ruolo) {
switch(ruolo) {
case "POR":return 50;
case "DIF":return 150;
case "CEN":return 300;
case "ATT":return 500;
default :return -1;

}
}
private void inputAcquisto() {
    boolean error = false;
    JComboBox<String> ruoloField = new JComboBox<>(ruoli);
    JTextField nomeField = new JTextField();
    JTextField cognomeField = new JTextField();
    JSpinner numeroSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1)); // Utilizzo di JSpinner per il numero del giocatore
    JCheckBox ckb = new JCheckBox("Deseleziona costo");
    JSpinner costoSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, Double.MAX_VALUE, 0.5)); // Utilizzo di JSpinner per il costo del giocatore
    JCheckBox ckbBench = new JCheckBox();
    Dimension spinnerSize = new Dimension(100, costoSpinner.getPreferredSize().height);
    costoSpinner.setPreferredSize(spinnerSize);
numeroSpinner.addChangeListener(new ChangeListener() {

	@Override
	public void stateChanged(ChangeEvent e) {
	for(CerchioGiocatore cg:giocatori) {
		if(((int)numeroSpinner.getValue())==cg.getNumero()) {
			numeroSpinner.setValue(((int)numeroSpinner.getValue())+1);
		}
	}
	for(CerchioGiocatore cg:panchinari) {
		if(((int)numeroSpinner.getValue())==cg.getNumero()) {
			numeroSpinner.setValue(((int)numeroSpinner.getValue())+1);
		}
	}
		
	}
	
});
    ckb.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            costoSpinner.setEnabled(!ckb.isSelected());
            costoSpinner.setValue(0.0);        }
    });

    

    Object[] message = {"Numero:", numeroSpinner, "Nome:", nomeField, "Cognome:", cognomeField, "Ruolo:", ruoloField,
            ckb, "Costo:", costoSpinner,"Aggiungi in panchina:", ckbBench};

    int option = JOptionPane.showConfirmDialog(this, message, "Inserisci dati giocatore",
            JOptionPane.OK_CANCEL_OPTION);

    if (option == JOptionPane.OK_OPTION) {
        int numero = (Integer) numeroSpinner.getValue(); // Otteniamo il numero del giocatore da JSpinner
        double costo = (Double) costoSpinner.getValue(); // Otteniamo il costo del giocatore da JSpinner
        String nome = nomeField.getText();
        String cognome = cognomeField.getText();
        String ruolo = (String) ruoloField.getSelectedItem();
        

 

        if (!error && controlloInput(nome, cognome, error, costo, numero+"")) {
        	JOptionPane.showMessageDialog(campo, "GIOCATORE AGGIUNTO", "SUCCESSO", JOptionPane.INFORMATION_MESSAGE);
            economia -= costo;
            CerchioGiocatore nuovoGiocatore = new CerchioGiocatore(posAboutRole(ruolo), 50, numero, nome, cognome, ruolo, costo);
            if (ckbBench.isSelected()) {
                panchinari.add(nuovoGiocatore);
            } else {
                giocatori.add(nuovoGiocatore);
            }
            repaint();
        }
    }
}



	public static void main(String[] args) {
		try {
		SwingUtilities.invokeLater(() -> {
			 bench = new JButton();
			formattaBottone(Color.BLUE, bench, campo, 360);
			ImageIcon iic = new ImageIcon(CampoDiCalcio.class.getResource("panchina.jpg"));
		
			Image fotos = iic.getImage().getScaledInstance(bench.getWidth() - 20, bench.getHeight() - 20,
					Image.SCALE_DEFAULT);
			iic.setImage(fotos);
			bench.setIcon(iic);
			bench.setToolTipText("Clicca per visionare i giocatori messi in panchina");
			bench.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					if (panchinari.size() > 0) {
						campo.inputPanchina();
					} else {
						JOptionPane.showMessageDialog(null, "NON HAI ANCORA INSERITO PANCHINARI", "ERRORE",
								JOptionPane.ERROR_MESSAGE);

					}
				
				}
			});
			
			
			
//COMPONENTE BUDGET
			 budget = new JButton();
			formattaBottone(null, budget, campo, 240);
			ImageIcon ic = new ImageIcon(CampoDiCalcio.class.getResource("imagesU.png"));
			//ImageIcon ic = new ImageIcon(
			//		"C:\\Users\\IO\\Desktop\\GIORGIO A SCUOLA\\progPazzi\\CapolavoroPrep\\src\\imagesU.png");
			Image foto = ic.getImage().getScaledInstance(budget.getWidth(), budget.getHeight(), Image.SCALE_DEFAULT);
			ic.setImage(foto);

			budget.addActionListener(new ActionListener() {
			    boolean start = false;
			    String resBudget = "";

			    public void actionPerformed(ActionEvent evt) {
			        JSpinner valBudget = new JSpinner(new SpinnerNumberModel(economia, 0.0, Double.MAX_VALUE, 0.5));
			        valBudget.setPreferredSize(new Dimension(110, 20));

			        JPanel pan = new JPanel();
			        pan.add(valBudget);

			        int opt = JOptionPane.showConfirmDialog(campo, pan, "INPUT BUDGET", JOptionPane.OK_CANCEL_OPTION);
			        if (opt == JOptionPane.OK_OPTION) {
			            try {
			                economia = (Double) valBudget.getValue();
			            } catch (Exception e) {
			                JOptionPane.showMessageDialog(null, "INSERISCI UN BUDGET VALIDO", "ERRORE", JOptionPane.ERROR_MESSAGE);
			            }
			        }
			    }
			});

		
			budget.setIcon(ic);
			budget.setToolTipText("Clicca per visionare le tue finanze");

			 signingButton = new JButton("ACQUISTO");
			signingButton.setToolTipText("premi il bottone per acquistare un giocatore per tua squadra");

			signingButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					if(giocatori.size()<11) {
					campo.inputAcquisto();
					}else {
						JOptionPane.showMessageDialog(campo, "TROPPI GIOCATORI NEL CAMPO",
								  "ERRORE", JOptionPane.ERROR_MESSAGE);
					}
				
				}
			});
			
			 saleButton = new JButton("CESSIONE");
			saleButton.setToolTipText("premi il bottone per cedere un giocatore della tua squadra");
			saleButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					CerchioGiocatore delete = campo.inputCessione();

					if (giocatori.contains(delete)) {
						
						giocatori.remove(delete);
						

					} else {
						panchinari.remove(delete);
					}
					campo.repaint();

			
				}
			});
			//COMPONENTE ROSA
			 rosaButton = new JButton("ROSA");
			rosaButton.setToolTipText("Visualizza una panoramica della tua rosa");
			rosaButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					if(giocatori.size()>0||panchinari.size()>0) {
					String[] columns = { "NOME", "COGNOME", "RUOLO", "NUMERO" };

					DefaultTableModel model = new DefaultTableModel(columns, 0) {
						public boolean isCellEditable(int row, int columns) {
							return false;
						}
					};
					for (CerchioGiocatore giocatore : giocatori) {
						Object[] riga = { giocatore.getNome(), giocatore.getCognome(), giocatore.getRuolo(),
								giocatore.getNumero() };
						model.addRow(riga);
					}
					for (CerchioGiocatore giocatore : panchinari) {
						Object[] riga = { giocatore.getNome(), giocatore.getCognome(), giocatore.getRuolo(),
								giocatore.getNumero() };
						model.addRow(riga);
					}

					JTable tab = new JTable(model);

					JScrollPane scrollPane = new JScrollPane(tab);

					JOptionPane.showConfirmDialog(campo, scrollPane, null, JOptionPane.CLOSED_OPTION);
					}else {
						
							JOptionPane.showMessageDialog(campo, "NON CI SONO GIOCATORI DA VISUALIZZARE!", "ERRORE", JOptionPane.ERROR_MESSAGE);
				
					}
				}
			});
			formattaBottone(Color.GREEN, signingButton, campo, 0);
			formattaBottone(Color.RED, saleButton, campo, 120);

			formattaBottone(Color.YELLOW, rosaButton, campo, 480);

			campo.setVisible(true);
		});
	
		 mod=new JButton("MATCH");
		 
		formattaBottone(null,mod,campo,600);
		mod.setToolTipText("Simula la tua partita live");
		JLabel lblTime=new JLabel();
		
			Timer secTimer=new Timer(1000, new ActionListener() {
				int cont=0,minutes,seconds;
				public void actionPerformed(ActionEvent e) {
					
					lblTime.setBounds(LARGHEZZA_CAMPO/2-23, 0, 40, 20);
					lblTime.setBackground(Color.GRAY);
					lblTime.setBorder(new LineBorder(Color.BLUE));
					lblTime.setForeground(Color.BLACK);
lblTime.setOpaque(false);
				
					 campo.add(lblTime);
					cont++;	
	                lblTime.setText(cont+"");
	                System.out.println("Timer: " + cont);
	                
                      minutes =  45 +(cont / 60);
                      seconds = cont % 60;
                     String formattedTime = String.format("%02d:%02d", minutes, seconds);
                     lblTime.setText(formattedTime);
                     if(minutes==90) {//modifica
                    	 ((Timer) e.getSource()).stop(); 
                    		mod.setEnabled(true);
        	            	//bench.setEnabled(false);
        	            	budget.setEnabled(true);
        	            	signingButton.setEnabled(true);
        	            	rosaButton.setEnabled(true);
        	            	saleButton.setEnabled(true);
                    	 campo.remove(lblTime);
                    	 campo.repaint();
                    	 cont=0;
				}
                     
				}
				
			});
		 Timer timer = new Timer(1000, new ActionListener() {
	            int count = 0,minutes,seconds;

	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	mod.setEnabled(false);
	            	//bench.setEnabled(false);
	            	budget.setEnabled(false);
	            	signingButton.setEnabled(false);
	            	rosaButton.setEnabled(false);
	            	saleButton.setEnabled(false);

	                count++;
	                lblTime.setText(count+"");
	                System.out.println("Timer: " + count);
	                
                      minutes = count / 60;
                      seconds = count % 60;
                     if(minutes==45) {//modifica
                    	 ((Timer) e.getSource()).stop();
                    	 campo.remove(lblTime);
                    	 JButton restart=new JButton();
                    //	 restart.setEnabled(true);
                    	 ImageIcon icon = new ImageIcon(CampoDiCalcio.class.getResource("CatturaMatita.png"));
                         
                         // Impostare l'icona sul JButton
                         restart.setIcon(icon);
                    	
                    	restart.addActionListener(new ActionListener() {
                    		public void actionPerformed(ActionEvent e) {
                    			secTimer.start();
                    			campo.remove(restart);
                    			campo.repaint();
                    			count=0;
                    		}
                    		
                    	});
                    	 restart.setBounds(LARGHEZZA_CAMPO/2, 0, 40, 20);
                    	//jhnijn
                    	 Timer tim = new Timer(10, new ActionListener() {
                             boolean evidenziato = false;

                             @Override
                             public void actionPerformed(ActionEvent e) {
                                 if (evidenziato) {
                                     restart.setBorderPainted(false);
                                 } else {
                                     restart.setBorderPainted(true);
                                 }
                                 evidenziato = !evidenziato;
                             }
                         });
                        // timer.setRepeats(true);
                         tim.start();
                     
                    	 //mettilo sopra
                    	 campo.add(restart);
                    	 campo.repaint();
                     }
                     String formattedTime = String.format("%02d:%02d", minutes, seconds);
                     lblTime.setText(formattedTime);
	            }
	        });
		mod.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent evt) {
				if(giocatori.size()==11) {
				
				lblTime.setBounds(LARGHEZZA_CAMPO/2-23, 0, 40, 20);
				lblTime.setBackground(Color.GRAY);
				lblTime.setBorder(new LineBorder(Color.BLUE));
				lblTime.setForeground(Color.BLACK);
lblTime.setOpaque(false);
			
				 campo.add(lblTime);
			        // Avvio del timer
			        timer.start();
				}else {
					JOptionPane.showMessageDialog(campo, "CI DEVONO ESSERE 11 GIOCATORI", "MATCH", JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		
	}catch (Exception e) {
	}
	
	
	}

	public boolean controlloInput(String nome, String cognome, boolean error, double costo,String numero) {
	String warnings="";
	boolean warn=false;
	if((nome.equals("")) || (cognome.equals(""))) {
		warn=true;
		warnings+="-NOME E COGNOME NON VALIDI"+"\n";
	}
	if(error) {
		warn=true;
		warnings+="IL NUMERO INSERITO E' GIA' STATO ASSEGNATO"+"\n";
		
	}
	if (economia - costo < 0) {
		warn=true;
		warnings+="IL BUDGET NON BASTA"+"\n";
	}
	if(numero.length()>2){
			warn=true;
			warnings+="IL FORMATO DEL NUMERO E' DI MASSIMO 2 CIFRE"+"\n";

		}
	if(numero.equals("0")||numero.equals("88")){
		warn=true;
		warnings+="IL NUMERO E' IRREGOLARE"+"\n";
		}
	if(warn) {
		System.out.println("jdbfkjsd");
		JOptionPane.showMessageDialog(campo, "SONO STATI RILEVATI I SEGUENTI ERRORI"+"\n"+warnings, "ERRORE", JOptionPane.WARNING_MESSAGE);
		return false;
		}else {
			return true;
		
	}
	}


	private boolean controlloSovrapposizione(int x, int y) {
		for (CerchioGiocatore giocatore : giocatori) {
			if (giocatore.contienePunto(x, y)) {
				return false;
			}
		}
		return true;
	}

	public CerchioGiocatore inputCessione() {

		 Set<CerchioGiocatore> setGioc = new HashSet<>(giocatori);
	        setGioc.addAll(panchinari);

	        JSpinner costoSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, Double.MAX_VALUE, 0.5));
	        Dimension spinnerSize = new Dimension(100, costoSpinner.getPreferredSize().height);
	        costoSpinner.setPreferredSize(spinnerSize);
	        JComboBox<CerchioGiocatore> set = new JComboBox<>(setGioc.toArray(new CerchioGiocatore[0]));
	        Object[] mess = {set, "Valore cessione:", costoSpinner};

	        if (giocatori.size() > 0 || panchinari.size() > 0) {
	            int option = JOptionPane.showConfirmDialog(campo, mess, "Inserisci dati cessione", JOptionPane.OK_CANCEL_OPTION);
	            if (option == JOptionPane.OK_OPTION) {
	                double valore = (Double) costoSpinner.getValue();
	                economia += valore;
	                return (CerchioGiocatore) set.getSelectedItem();
	            }
	        } else {
	            JOptionPane.showMessageDialog(campo, "NESSUN GIOCATORE IN ROSA");
	        }
	        return null;
	}

	public void sheetLayout(CerchioGiocatore gioc) {
	     JTextField nomeField = new JTextField(gioc.getNome(), 10);
	        JTextField cognomeField = new JTextField(gioc.getCognome(), 10);
	        JSpinner numeroSpinner = new JSpinner(new SpinnerNumberModel(gioc.getNumero(), 0, 99, 1));

	        JComboBox<String> nuoviRuoli = new JComboBox<>(ruoli);
	        nuoviRuoli.setSelectedItem(gioc.getRuolo());
	        nuoviRuoli.setPreferredSize(new Dimension(120, 20));

	        JPanel panelScheda = new JPanel(new GridBagLayout());
	        GridBagConstraints gbc = new GridBagConstraints();
	        gbc.insets = new Insets(8, 8, 8, 8);

	        // Label e campo Numero
	        gbc.gridx = 0;
	        gbc.gridy = 0;
	        panelScheda.add(new JLabel("Numero:"), gbc);
	        gbc.gridx = 1;
	        panelScheda.add(numeroSpinner, gbc);

	        // Label e campo Nome
	        gbc.gridx = 0;
	        gbc.gridy = 1;
	        panelScheda.add(new JLabel("Nome:"), gbc);
	        gbc.gridx = 1;
	        panelScheda.add(nomeField, gbc);

	        // Label e campo Cognome
	        gbc.gridx = 0;
	        gbc.gridy = 2;
	        panelScheda.add(new JLabel("Cognome:"), gbc);
	        gbc.gridx = 1;
	        panelScheda.add(cognomeField, gbc);

	        // Label e campo Ruolo
	        gbc.gridx = 0;
	        gbc.gridy = 3;
	        panelScheda.add(new JLabel("Ruolo:"), gbc);
	        gbc.gridx = 1;
	        panelScheda.add(nuoviRuoli, gbc);

	        int option = JOptionPane.showConfirmDialog(campo, panelScheda, "Inserisci dati giocatore", JOptionPane.OK_CANCEL_OPTION);
	        if (option == JOptionPane.OK_OPTION) {
	            gioc.setNumero((Integer) numeroSpinner.getValue());
	            gioc.setRuolo((String) nuoviRuoli.getSelectedItem());
	            gioc.setCognome(cognomeField.getText());
	            gioc.setNome(nomeField.getText());
	            repaint();
	        }
	}

	public void inputPanchina() {

		JComboBox scheda = new JComboBox(panchinari.toArray());

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 8, 8, 8);
		gbc.gridx = 0;
		gbc.gridwidth = 4;
		panel.add(scheda, gbc);
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;
		JRadioButton rdbCampo = new JRadioButton("Torna in campo");
		rdbCampo.setSelected(true);
		panel.add(rdbCampo, gbc);
		gbc.gridx = 1;
		JRadioButton rdbInfo = new JRadioButton("Scheda giocatore");
		panel.add(rdbInfo, gbc);
		gbc.gridx = 2;
		JRadioButton rdbCessione = new JRadioButton("Cedi giocatore");
		panel.add(rdbCessione, gbc);
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(rdbCessione);
		buttonGroup.add(rdbInfo);
		buttonGroup.add(rdbCampo);

		// System.out.println(scheda.getSelectedItem());
		int res = JOptionPane.showConfirmDialog(null, panel, "Inserisci dati giocatore", JOptionPane.OK_CANCEL_OPTION);
		if (res == JOptionPane.OK_OPTION) {
			if (rdbCampo.isSelected()) {

				CerchioGiocatore p = (CerchioGiocatore) scheda.getSelectedItem();
				giocatori.add(p);
				panchinari.remove(p);
				campo.repaint();

			} else if (rdbInfo.isSelected()) {
				sheetLayout(((CerchioGiocatore) scheda.getSelectedItem()));

				

			}else if(rdbCessione.isSelected()) {
				sellByBenchPitch((CerchioGiocatore) scheda.getSelectedItem(), panchinari);
			}
		}

	}

	public static void sellByBenchPitch(CerchioGiocatore gioc, Vector<CerchioGiocatore> vec) {
		 JSpinner txt = new JSpinner(new SpinnerNumberModel(0.0, 0.0, Double.MAX_VALUE, 0.5));
		 Dimension spinnerSize = new Dimension(100, txt.getPreferredSize().height);
		    txt.setPreferredSize(spinnerSize);
	        Object[] mess = {"VALORE GIOCATORE:", txt};
	        CerchioGiocatore delete = gioc;

	        int opt = JOptionPane.showConfirmDialog(campo, mess, "INPUT CESSIONE", JOptionPane.OK_CANCEL_OPTION);
	        if (opt == JOptionPane.OK_OPTION) {
	            try {
	                economia += (Double) txt.getValue();
	                vec.remove(delete);
	            } catch (Exception e) {
	                JOptionPane.showMessageDialog(campo, "VALORE SBAGLIATO");
	            }
	            campo.repaint();
	        }
	}

	public static void formattaBottone(Color cl, JButton b, CampoDiCalcio cdc, int x) {
		Font font = new Font("Arial", Font.BOLD, 17);

		b.setForeground(cl);
		b.setBackground(Color.BLACK);
		b.setFont(font);
		b.setBounds(x, 469, 120, 50);
		cdc.getContentPane().add(b);
	}

	public class CerchioGiocatore {
		private String nome, cognome, ruolo;
		private int x, numero;
		private double costo;
		private int y;
		static private int raggio = 20;
		private boolean trascinando;

//AGGIUNGI VALORE
		public CerchioGiocatore(int x, int y, int numero, String nome, String cognome, String ruolo, double costo) {
			this.x = x;
			this.y = y;
			this.costo = costo;
			this.numero = numero;
			this.nome = nome;
			this.cognome = cognome;
			this.ruolo = ruolo;
		}

		public double getCosto() {
			return costo;
		}

		public void setCosto(int costo) {
			this.costo = costo;
		}

		public String toString() {
			return nome + " " + cognome;
		}

		public int getY() {
			return y;
		}

		public int getX() {
			return x;
		}

		public boolean contienePunto(int px, int py) {
			int distanzaQuad = (x - px) * (x - px) + (y - py) * (y - py);
			int raggioQuad = raggio * raggio;
			return distanzaQuad <= raggioQuad;
		}

		private Color setColore() {
			switch (ruolo) {
			case "POR":
				return Color.ORANGE;
			case "DIF":
				return Color.YELLOW;
			case "CEN":
				return Color.RED;
			case "ATT":
				return Color.BLACK;
			default:
				return Color.BLUE;
			}
		}

		public void draw(Graphics g) {

			g.setColor(setColore());
			g.fillOval(x - raggio, y - raggio, 2 * raggio, 2 * raggio);

			g.setColor(Color.WHITE);
			Font font = new Font("Arial", Font.BOLD, 15);
			g.setFont(font);
			// Ottieni le informazioni sul font
			FontMetrics fontMetrics = g.getFontMetrics();

			// Ottieni la larghezza della stringa
			int stringWidth = fontMetrics.stringWidth(String.valueOf(numero));

			// Modifica la posizione del disegno in base alla lunghezza della stringa
			g.drawString(String.valueOf(numero), x - stringWidth / 2, y + 5);
			// g.drawString(String.valueOf(numero), x - 5, y + 5);
		}

		public String getRuolo() {
			return ruolo;
		}

		public void setRuolo(String ruolo) {
			this.ruolo = ruolo;
		}

		public String getNome() {
			return nome;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public String getCognome() {
			return cognome;
		}

		public void setCognome(String cognome) {
			this.cognome = cognome;
		}

		public boolean isTrascinando() {
			return trascinando;
		}

		public void setTrascinando(boolean trascinando) {
			this.trascinando = trascinando;
		}

		public void setX(int x) {
			this.x = x;
		}

		public void setY(int y) {
			this.y = y;
		}

		public void setPosizione(int x, int y) {
			this.x = x;
			
			this.y = y;
			
		}

		public int getNumero() {
			return numero;
		}

		public void setNumero(int numero) {
			this.numero = numero;
		}
	}
}

package vuongnguyen;

import vuongnguyen.DataLoader.DataReader;
import vuongnguyen.Dictionary.BloomFilter;
import vuongnguyen.Dictionary.DictionaryList;
import vuongnguyen.Dictionary.Trie;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Set;

public class App {
    private JTextField txtInput;
    private JButton btnCheck;
    private JList lstPredictiveText;
    private JPanel panelMain;
    private JRadioButton radSearchTrie;
    private JRadioButton radSearchBloomFilter;
    private JScrollPane scrollList;
    private static final int MAX_ITEM_DISPLAY = 5;

    public App() {

        configGUI();

        btnCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String key = (lstPredictiveText.getSelectedIndex() == -1)?txtInput.getText():lstPredictiveText.getSelectedValue().toString();
                if(key.equals("") == false){
                    DictionaryList dictionaryList = new DictionaryList();
                    String strNotExist = " is not exist";
                    String strExist = " is exist";
                    String strMaybe = " (maybe)";

                    if (radSearchBloomFilter.isSelected() == true) {
                        dictionaryList.setDictionary(BloomFilter.getInstance());
                        strExist+=strMaybe;
                    } else {
                        dictionaryList.setDictionary(Trie.getInstance());
                    }

                    if(dictionaryList.contains(key)){
                        JOptionPane.showMessageDialog(null, key + strExist);
                    }
                    else {
                        JOptionPane.showMessageDialog(null, key + strNotExist);
                    }
                }
                else{
                    JOptionPane.showMessageDialog(null, "Input not null");
                }
            }
        });

        txtInput.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void warn(){
                if(txtInput.getText().equals("") == false){
                    lstPredictiveText.setEnabled(true);
                    Set<String> listWord = Trie.getInstance().query(txtInput.getText(), MAX_ITEM_DISPLAY);
                    lstPredictiveText.setListData(listWord.toArray());
                }
                else{
                    lstPredictiveText.setEnabled(false);
                    lstPredictiveText.setListData(new String[]{""});
                }
            }
        });
    }

  public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        frame.setContentPane(new App().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(300,300);
        frame.setLocationRelativeTo(null);
  }

  private void configGUI(){
      lstPredictiveText.setEnabled(false);
      lstPredictiveText.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      lstPredictiveText.setLayoutOrientation(JList.VERTICAL);

      ButtonGroup buttonGroup = new ButtonGroup();
      buttonGroup.add(radSearchTrie);
      buttonGroup.add(radSearchBloomFilter);

      scrollList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      scrollList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

      radSearchTrie.setSelected(true);
      radSearchBloomFilter.setSelected(false);

      try {
          DataReader.read(System.getProperty("user.dir") + "/blogs");
      } catch (IOException e) {
          e.printStackTrace();
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
  }
}

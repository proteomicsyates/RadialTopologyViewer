//NetQuant is a graph viewer which allows interactive editing of networks
//(edges and nodes) and connects to databases and provides quantitative analysis.
//NetQuant is based on Medusa developed by Sean Hopper <http://www.bork.embl.de/medusa>
//
//Copyright (C) 2011 Diego Calzolari
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful, but 
//WITHOUT ANY WARRANTY; without even the implied warranty of 
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
//Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.


package netQuant;

import javax.swing.*;

/**
 * abstract class to manage a progress bar
 * @author diego
 *
 */
abstract class FGProgBar {
    private JFrame parent;
    private String text;
    private JWindow win;
    private JProgressBar prog;
   
    public abstract void doWork();

    public FGProgBar(JFrame parent, String text) throws IllegalArgumentException {
        this.parent = parent;
        if (this.parent == null) {
            throw new IllegalArgumentException("parent == null !");
        }

        this.text = text;
        if (this.text == null) {
            this.text = "Processing";
        }

        prog = new JProgressBar();
        //prog.setIndeterminate(indeterminate); // todo TA TILLBAKS TILL JRE1.4.1
        prog.setStringPainted(true);
        prog.setString(text);

        JPanel progPanel = new JPanel();
        progPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        progPanel.add(prog);

        win = new JWindow(parent);
        win.setSize(200, 25);
        //GUIHelper.centerWindow(win); //denna method lagger applicationen mitt pa skarmen
        win.getContentPane().add(progPanel);
        win.pack();
    }

    public void setIndeterminate(boolean value) {
    }
    public void setProgress(int value){
	prog.setValue(value);
    }

    public void show() {
        win.setVisible(true);

        new Thread(new Runnable() {
            public void run() {
                doWork();

                win.setVisible(false);
                win.dispose();
            }
        }).start();
	}
}


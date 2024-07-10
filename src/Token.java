/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author FERNANDO
 */
// Clase para representar un token
// Clase Token
public class Token {
    private String type;
    private String value;
    private int linea;
    private int index;

    public Token(String type, String value, int linea, int index) {
        this.type = type;
        this.value = value;
        this.linea = linea;
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLinea() {
        return linea;
    }

    public int getIndex() {
        return index;
    }   
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author FERNANDO
 */
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Scanner {
    private String code;
    private List<String> palabrasReservadas;
    private int linea;
    private Iterator<Token> tokenGenerator;
    private Token lastToken;

    public Scanner(String code) {
        this.code = code;
        this.palabrasReservadas = Arrays.asList("entero", "real", "si", "sino", "mientras", "fmientras", "fsi", "imprime", "verdadero", "falso");
        this.linea = 1;
        this.tokenGenerator = null;
        this.lastToken = null;
    }

    public Scanner(){
        
    }
    
    // Método para generar tokens
    private List<Token> getTokens() {
        List<Token> tokens = new ArrayList<>();
        StringBuilder lexema = new StringBuilder();

        for (int i = 0; i < code.length(); i++) {
            char ch = code.charAt(i);

            if (ch == '\n') {
                if (lexema.length() > 0) {
                    tokens.add(clasificarLexema(lexema.toString(), i));
                    lexema.setLength(0);
                }
                tokens.add(clasificarLexema("\n", i));
                linea++;
            } else if (Character.isWhitespace(ch) || ",()=+-*/^<>|&".indexOf(ch) >= 0) {
                if (lexema.length() > 0) {
                    tokens.add(clasificarLexema(lexema.toString(), i));
                    lexema.setLength(0);
                }
                if (!Character.isWhitespace(ch)) {
                    tokens.add(clasificarLexema(Character.toString(ch), i + 1));
                }
            } else if (Character.isLetterOrDigit(ch) || ch == '.') {
                lexema.append(ch);
            } else {
                tokens.add(clasificarLexema(Character.toString(ch), i + 1));
            }
        }

        if (lexema.length() > 0) {
            tokens.add(clasificarLexema(lexema.toString(), code.length()));
        }

        return tokens;
    }

    // Método para clasificar un lexema
    private Token clasificarLexema(String lexema, int index) {
        if (palabrasReservadas.contains(lexema)) {
            return new Token(TokenType.PALABRA_RESERVADA, lexema, linea, index);
        }
        if (lexema.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
            return new Token(TokenType.ID, "ID", linea, index);
        }
        if (lexema.matches("^[0-9]+(\\.[0-9]+)?$")) {
            return new Token(TokenType.NUM, "NUM", linea, index);
        }
        if (lexema.matches("[=+\\-*/^<>|&]")) {
            return new Token(TokenType.OPERADOR, lexema, linea, index);
        }
        if (lexema.matches("[\n,()]")) {
            return new Token(TokenType.SIMBOLO, lexema, linea, index);
        }

        Token tokenDesconocido = new Token(TokenType.DESCONOCIDO, lexema, linea, index);
        displayError("Error Léxico", tokenDesconocido);
        return tokenDesconocido;
    }

    // Método para obtener el siguiente token
    public Token getToken() {
        if (tokenGenerator == null) {
            tokenGenerator = getTokens().iterator();
        }

        if (tokenGenerator.hasNext()) {
            Token actualToken = tokenGenerator.next();
            if (!(actualToken.getValue().equals("\n") && (lastToken == null || lastToken.getValue().equals("\n")))) {
                lastToken = actualToken;
                return actualToken;
            } else {
                return getToken();
            }
        } else {
            return new Token(TokenType.EOF, "EOF", linea, code.length());
        }
    }

    // Método para mostrar un error
    private void displayError(String message, Token token) {
        System.err.println(message + " en la línea " + token.getLinea() + " en el índice " + token.getIndex() + ": " + token.getValue());
    }

    public static void main(String[] args) {
        // Definir el nombre del archivo aquí.
        String fileName = "archivo.txt";  // Asegúrate de que este archivo esté en la misma ruta que el proyecto
        String code = "";

        try {
            code = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            System.err.println("Error leyendo el archivo: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(code);

        Token token;
        do {
            token = scanner.getToken();
            System.out.println("Token: " + token.getType() + " Valor: " + token.getValue() + " ----> Línea: " + token.getLinea() + " Índice: " + token.getIndex());
        } while (token.getType() != TokenType.EOF);
    }
}
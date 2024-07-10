/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author FERNANDO
 */
import java.util.*;

public class Parser {
    private Scanner scanner;
    private Token currentToken;
    private Stack<Token> pila;
    private String estadoAPD;

    private Map<String, Map<String, String>> AFD1;
    private Map<String, Map<String, String>> AFD2;
    private Map<String, Map<String, String>> AFD3;
    private Map<String, Map<String, String>> AFD4;
    private Map<String, Map<String, Map<String, Map<String, String>>>> APD;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        this.currentToken = null;

        // Inicialización de la pila y del estado del APD
        this.pila = new Stack<>();
        this.pila.push(new Token("P0", "P0", 0, 0));
        this.estadoAPD = "q0";

        // Definición de los autómatas de estados finitos (AFD) para distintas estructuras
        this.AFD1 = new HashMap<>();
        this.AFD2 = new HashMap<>();
        this.AFD3 = new HashMap<>();
        this.AFD4 = new HashMap<>();
        this.APD = new HashMap<>();

        // AFD1 para declaración de variables
        Map<String, String> afd1Q0 = new HashMap<>();
        afd1Q0.put("entero", "q1");
        afd1Q0.put("real", "q1");
        AFD1.put("q0", afd1Q0);

        Map<String, String> afd1Q1 = new HashMap<>();
        afd1Q1.put("ID", "q2");
        AFD1.put("q1", afd1Q1);

        Map<String, String> afd1Q2 = new HashMap<>();
        afd1Q2.put(",", "q1");
        afd1Q2.put("=", "q3");
        afd1Q2.put("\n", "qf");
        afd1Q2.put("EOF", "qf");
        AFD1.put("q2", afd1Q2);

        Map<String, String> afd1Q3 = new HashMap<>();
        afd1Q3.put("EXP", "q4");
        AFD1.put("q3", afd1Q3);

        Map<String, String> afd1Q4 = new HashMap<>();
        afd1Q4.put(",", "q1");
        afd1Q4.put("\n", "qf");
        afd1Q4.put("EOF", "qf");
        AFD1.put("q4", afd1Q4);

        // AFD2 para la función imprime
        Map<String, String> afd2Q0 = new HashMap<>();
        afd2Q0.put("imprime", "q1");
        AFD2.put("q0", afd2Q0);

        Map<String, String> afd2Q1 = new HashMap<>();
        afd2Q1.put("EXP", "q2");
        AFD2.put("q1", afd2Q1);

        Map<String, String> afd2Q2 = new HashMap<>();
        afd2Q2.put(",", "q1");
        afd2Q2.put("\n", "qf");
        afd2Q2.put("EOF", "qf");
        AFD2.put("q2", afd2Q2);

        // AFD3 para asignaciones
        Map<String, String> afd3Q0 = new HashMap<>();
        afd3Q0.put("ID", "q1");
        AFD3.put("q0", afd3Q0);

        Map<String, String> afd3Q1 = new HashMap<>();
        afd3Q1.put("=", "q2");
        AFD3.put("q1", afd3Q1);

        Map<String, String> afd3Q2 = new HashMap<>();
        afd3Q2.put("EXP", "q3");
        AFD3.put("q2", afd3Q2);

        Map<String, String> afd3Q3 = new HashMap<>();
        afd3Q3.put("\n", "qf");
        afd3Q3.put("EOF", "qf");
        AFD3.put("q3", afd3Q3);

        // AFD4 para condiciones
        Map<String, String> afd4Q0 = new HashMap<>();
        afd4Q0.put("(", "q1");
        AFD4.put("q0", afd4Q0);

        Map<String, String> afd4Q1 = new HashMap<>();
        afd4Q1.put("EXP", "q2");
        AFD4.put("q1", afd4Q1);

        Map<String, String> afd4Q2 = new HashMap<>();
        afd4Q2.put("<", "q3");
        afd4Q2.put(">", "q3");
        AFD4.put("q2", afd4Q2);

        Map<String, String> afd4Q3 = new HashMap<>();
        afd4Q3.put("EXP", "q4");
        AFD4.put("q3", afd4Q3);

        Map<String, String> afd4Q4 = new HashMap<>();
        afd4Q4.put(")", "q5");
        AFD4.put("q4", afd4Q4);

        Map<String, String> afd4Q5 = new HashMap<>();
        afd4Q5.put("\n", "qf");
        AFD4.put("q5", afd4Q5);

        // APD para estructuras de control
        Map<String, Map<String, Map<String, String>>> apdQ0 = new HashMap<>();

        Map<String, Map<String, String>> siTransitions = new HashMap<>();
        siTransitions.put("P0", createTransitionMap("q0", "+"));
        siTransitions.put("-", createTransitionMap("q0", "+"));
        apdQ0.put("si", siTransitions);

        Map<String, Map<String, String>> sinoTransitions = new HashMap<>();
        sinoTransitions.put("si", createTransitionMap("q0", "+"));
        apdQ0.put("sino", sinoTransitions);

        Map<String, Map<String, String>> fsiTransitions = new HashMap<>();
        fsiTransitions.put("sino", createTransitionMap("q0", "&&"));
        fsiTransitions.put("si", createTransitionMap("q0", "&"));
        apdQ0.put("fsi", fsiTransitions);

        Map<String, Map<String, String>> mientrasTransitions = new HashMap<>();
        mientrasTransitions.put("P0", createTransitionMap("q0", "+"));
        mientrasTransitions.put("-", createTransitionMap("q0", "+"));
        apdQ0.put("mientras", mientrasTransitions);

        Map<String, Map<String, String>> fmientrasTransitions = new HashMap<>();
        fmientrasTransitions.put("mientras", createTransitionMap("q0", "&"));
        apdQ0.put("fmientras", fmientrasTransitions);

        APD.put("q0", apdQ0);
    }

    // Crea un mapa de transición
    private Map<String, String> createTransitionMap(String estado, String pila) {
        Map<String, String> transition = new HashMap<>();
        transition.put("estado", estado);
        transition.put("pila", pila);
        return transition;
    }

    // Pide el siguiente token al scanner y lo guarda en currentToken
    private void getToken() {
        this.currentToken = this.scanner.getToken();
    }

    // Evalúa un AFD dado
    private boolean evaluarAFD(Map<String, Map<String, String>> afd) {
        String estado = "q0";
        do {
            if (afd.containsKey(estado) && afd.get(estado).containsKey(this.currentToken.getValue())) {
                estado = afd.get(estado).get(this.currentToken.getValue());
                getToken();
            } else if (afd.containsKey(estado) && afd.get(estado).containsKey("EXP")) {
                if (evaluarExpresion()) {
                    estado = afd.get(estado).get("EXP");
                } else {
                    displayError("Error Sintáctico", this.currentToken, "Error en la " + afd.get("name"));
                    return false;
                }
            } else {
                displayError("Error Sintáctico", this.currentToken, "Error en la " + afd.get("name"));
                return false;
            }
        } while (this.currentToken != null && !estado.equals("qf"));

        return true;
    }

    // Evalúa el APD de estructuras de control
    private boolean evaluarAPD() {
        Token topePila = this.pila.peek();
        Map<String, Map<String, String>> transitions = this.APD.get(this.estadoAPD).get(this.currentToken.getValue());
        Map<String, String> transicion = transitions != null ? transitions.get(topePila.getValue()) : null;

        if (transicion == null) {
            transitions = this.APD.get(this.estadoAPD).get(this.currentToken.getValue());
            transicion = transitions != null ? transitions.get("-") : null;
        }

        if (transicion != null) {
            this.estadoAPD = transicion.get("estado");
            actualizarPila(transicion.get("pila"));
            return true;
        } else {
            displayError("Error Sintáctico", this.currentToken, "Error de estructura de control");
            return false;
        }
    }

    // Actualiza la pila del APD según el token apilado
    private void actualizarPila(String tokenApilado) {
        switch (tokenApilado) {
            case "&":
                this.pila.pop();
                break;
            case "&&":
                this.pila.pop();
                this.pila.pop();
                break;
            case "+":
                this.pila.push(this.currentToken);
                break;
            case "-":
                break;
            default:
                break;
        }
    }

    // Evalúa si el APD ha finalizado correctamente
    private boolean evaluarFinalAPD() {
        if (!this.pila.peek().getValue().equals("P0")) {
            displayError("Error Sintáctico", this.pila.peek(), "Error de estructura de control");
            return false;
        }
        return true;
    }

    // Evalúa una expresión aritmética
    private boolean evaluarExpresion() {
        try {
            E();
        } catch (Exception e) {
            displayError("Error Sintáctico", this.currentToken, "Error en la expresión aritmética");
            return false;
        }
        return true;
    }

    // Verifica si el token actual es el esperado y avanza al siguiente
    private void match(String expectedToken) throws Exception {
        if (this.currentToken.getValue().equals(expectedToken)) {
            this.currentToken = this.scanner.getToken();
        } else {
            throw new Exception("Token inesperado. Se esperaba el token \"" + expectedToken + "\" pero se encontró: \"" + this.currentToken.getValue() + "\".");
        }
    }

    // ---- Parser LL(1) ----
    private void E() throws Exception {
        T();
        X();
    }

    private void X() throws Exception {
        switch (this.currentToken.getValue()) {
            case "+":
                match("+");
                E();
                break;
            case "-":
                match("-");
                E();
                break;
            case "EOF":
            case "\n":
            case ",":
            case "<":
            case ">":
            case ")":
                break;
            default:
                throw new Exception("Token inesperado en X: \"" + this.currentToken.getValue() + "\"");
        }
    }

    private void T() throws Exception {
        F();
        Y();
    }

    private void Y() throws Exception {
        switch (this.currentToken.getValue()) {
            case "*":
                match("*");
                T();
                break;
            case "/":
                match("/");
                T();
                break;
            case "+":
            case "-":
            case "EOF":
            case "\n":
            case ",":
            case "<":
            case ">":
            case ")":
                break;
            default:
                throw new Exception("Token inesperado en Y: \"" + this.currentToken.getValue() + "\"");
        }
    }

    private void F() throws Exception {
        G();
        Z();
    }

    private void Z() throws Exception {
        switch (this.currentToken.getValue()) {
            case "^":
                match("^");
                F();
                break;
            case "*":
            case "/":
            case "+":
            case "-":
            case "EOF":
            case "\n":
            case ",":
            case "<":
            case ">":
            case ")":
                break;
            default:
                throw new Exception("Token inesperado en Z: \"" + this.currentToken.getValue() + "\"");
        }
    }

    private void G() throws Exception {
        switch (this.currentToken.getValue()) {
            case "(":
                match("(");
                E();
                match(")");
                break;
            case "ID":
                match("ID");
                break;
            case "NUM":
                match("NUM");
                break;
            default:
                throw new Exception("Token inesperado en G: \"" + this.currentToken.getValue() + "\"");
        }
    }

    // Método principal para el análisis sintáctico
    public boolean parse() {
        getToken();
        while (this.currentToken != null && !this.currentToken.getType().equals("EOF")) {
            boolean success = false;

            if (Arrays.asList("si", "sino", "fsi", "mientras", "fmientras").contains(this.currentToken.getValue())) {
                if (this.currentToken.getValue().equals("si") || this.currentToken.getValue().equals("mientras")) {
                    success = evaluarAPD() && evaluarAFD(AFD4);
                } else {
                    success = evaluarAPD();
                    if (success) {
                        if (this.currentToken.getValue().equals("\n") || this.currentToken.getValue().equals("EOF")) {
                            getToken();
                        } else {
                            displayError("Error Sintáctico", this.currentToken);
                            return false;
                        }
                    }
                }
            } else if (this.currentToken.getValue().equals("entero") || this.currentToken.getValue().equals("real")) {
                success = evaluarAFD(AFD1);
            } else if (this.currentToken.getValue().equals("imprime")) {
                success = evaluarAFD(AFD2);
            } else if (this.currentToken.getValue().equals("ID")) {
                success = evaluarAFD(AFD3);
            }

            if (!success) {
                return false;
            }
        }

        return evaluarFinalAPD();
    }

// Método mejorado para mostrar errores con más detalles
private void displayError(String tipoError, Token token, String mensaje) {
    System.err.println(tipoError + " en la línea " + token.getLinea() + ", índice " + token.getIndex() + ". " + mensaje);
    System.err.println("Token problemático: " + token.getType() + " Valor: " + token.getValue());
    System.err.println("Estructura de control esperada: " + this.pila.peek().getValue());
}

// Sobrecarga para mostrar errores estándar
private void displayError(String tipoError, Token token) {
    System.err.println(tipoError + " en la línea " + token.getLinea() + ", índice " + token.getIndex() + ". Se encontró el token: \"" + token.getValue() + "\"");
}

    public static void main(String[] args) {
        // Definir el nombre del archivo aquí.
        String fileName = "archivo.txt";  // Asegúrate de que este archivo esté en la misma ruta que el proyecto
        String code = "";

        try {
            code = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(fileName)));
        } catch (java.io.IOException e) {
            System.err.println("Error leyendo el archivo: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(code);
        Parser parser = new Parser(scanner);

        if (parser.parse()) {
            System.out.println("Análisis sintáctico completado exitosamente.");
        } else {
            System.err.println("Hubo errores en el análisis sintáctico.");
        }
    }
}  
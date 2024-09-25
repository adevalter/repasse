package br.com.adeweb.repasse.core.execpetions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) { super(message); }
    public UserNotFoundException() { super("Usuário não logado"); }
}
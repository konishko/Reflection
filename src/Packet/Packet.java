package Packet;

public class Packet {
    private int i;
    private short s;
    private long l;
    private float f;
    private double d;
    private boolean bool;
    private char c;
    private byte b;
    private String str;
    private Packet p;

    public Packet(){
    }

    public Packet(int i, short s, long l, float f, double d, boolean bool, char c, byte b, String str, Packet p){
        this.i = i;
        this.s = s;
        this.f = f;
        this.l = l;
        this.d = d;
        this.bool = bool;
        this.c = c;
        this.b = b;
        this.str = str;
        this.p = p;
    }
}

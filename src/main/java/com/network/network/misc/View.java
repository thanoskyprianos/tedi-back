package com.network.network.misc;

public class View {
    public interface AsProfessional { }
    public interface AsAdmin extends AsProfessional { }
    public interface Inaccessible extends AsAdmin { }
}

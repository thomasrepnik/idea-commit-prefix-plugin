package ch.repnik.intellij.settings;

public enum Position {
    START("start"), END("end");

    private final String position;

    Position(String position){
        this.position = position;
    }

    public String getStringValue(){
        return this.position;
    }

    public static Position parse(String position){
        for (Position value : Position.values()) {
            if (value.position.equals(position)){
                return value;
            }
        }

        throw new IllegalArgumentException("Position '" + position + "' was not found in enum");
    }
}

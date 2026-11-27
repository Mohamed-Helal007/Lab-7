package ems.model;

public enum EmployeeType {

    FULL_TIME("Full-Time"),
    PART_TIME("Part-Time");

    private final String displayName;
    
    private EmployeeType(String displayName){
        this.displayName=displayName;
    }
    public String getDisplayName(){
        return displayName;
    }
    public String toString(){
        return displayName;
    }
    public static EmployeeType fromText(String text){
        if(text==null||text.trim().isEmpty()){
            throw new IllegalArgumentException("Employee type text cannot be null or empty");
        }
        String normalized=text.trim();
        for(EmployeeType type:values()){
            if (type.name().equalsIgnoreCase(normalized)
                    || type.displayName.equalsIgnoreCase(normalized)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown employee type: \"" + text + "\"");
    }
}

package IR;

public class METHOD_ENTRY {
    public String methodName;
    public String className;

    public METHOD_ENTRY(String methodName, String className) {
        this.methodName = methodName;
        this.className = className;
    }


    @Override
    public String toString() {
        return methodName;
    }
}
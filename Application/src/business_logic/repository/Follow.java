package business_logic.repository;

/**
 * Abstract Class Follow : mother class in a composite pattern to make a treelike structure
 * @author Adrien
 */
public abstract class Follow {

    private static transient Follow selectedDaD;
        public static Follow getSelectedDaD() { return selectedDaD; }
        public static void setSelectedDaD(Follow selectedDaD) { Follow.selectedDaD = selectedDaD; }

    abstract public String getName();
    abstract public void setName(String value);

    public void addFollow(Follow follow) { throw new UnsupportedOperationException("OperationNotSupportedException"); }
    public void deleteFollow(Follow follow) { throw new UnsupportedOperationException("OperationNotSupportedException"); }
}

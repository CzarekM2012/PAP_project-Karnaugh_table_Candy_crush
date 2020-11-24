public class Field
{
    int value;
    ReplacementSource replacementSource;
    public Field()
    {
        clear();
    }
    public Field(int value, ReplacementSource replacementSource)
    {
        this.value = value;
        this.replacementSource = replacementSource;
    }
    public void clear()
    {
        set(-1, ReplacementSource.Improper);
    }
    public void set(int value, ReplacementSource replacementSource)
    {
        this.value = value;
        this.replacementSource = replacementSource;
    }
    public int getValue(){return value;}
    public ReplacementSource getReplacementSource(){return replacementSource;}
    public boolean equals(Field objField)
    {
        return (value == objField.value && replacementSource.equals(replacementSource));
    }
    public static void main(String args[])
    {
        Field test = new Field(5, ReplacementSource.Top);
        System.out.print(test.getValue());
        if(test.getReplacementSource()==ReplacementSource.Top){System.out.println("t");}
        else if(test.getReplacementSource()==ReplacementSource.Bottom){System.out.println("b");}
        else if(test.getReplacementSource()==ReplacementSource.Left){System.out.println("l");}
        else if(test.getReplacementSource()==ReplacementSource.Right){System.out.println("r");}
    }
}
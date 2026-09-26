package Forms;

import javax.swing.*;

public class BaseFrame extends JFrame
{
    private JFrame parentFrame;

    public void setParentFrame(JFrame parentFrame)
    {
        this.parentFrame = parentFrame;
    }
    public BaseFrame()
    {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void goToSon(BaseFrame baseFrame)
    {
        baseFrame.setParentFrame(this);
        baseFrame.setVisible(true);
        this.dispose();
    }
    public void goToParent()
    {
        if (this.parentFrame == null) throw new IllegalStateException("ParentFrame is null,cannot go to parent");
        this.parentFrame.setVisible(true);
        this.dispose();
    }
}

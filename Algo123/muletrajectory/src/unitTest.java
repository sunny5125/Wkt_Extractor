package muletrajectory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class unitTest {

    public static void main(String args[]) throws IOException
    {
       
       for(int i=1;i<=30;i++)
        GroupFormation.getGroups(8,i*60);
        
    }

}

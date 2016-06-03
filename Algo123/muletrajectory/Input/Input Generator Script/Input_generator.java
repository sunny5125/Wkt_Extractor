import java.util.*;
import java.io.*;

class Input_generator
{
	public static void main(String args[])throws IOException
	{
		Scanner scan = new Scanner(new File("input.txt"));
		int v= scan.nextInt();
		int mat[][]= new int[v+1][v+1];

		for(int i=1;i<=v;i++)
			for(int j=1;j<=v;j++)
				mat[i][j]=-1;

		while(scan.hasNextInt())
		{
			int p= scan.nextInt();
			int q= scan.nextInt();
			int w= scan.nextInt();
			mat[p][q]=w;
			mat[p][q]=w;
		}

		for(int i=1;i<=v;i++)
		{
			for(int j=1;j<=v;j++)
			{
				System.out.print(mat[i][j]+" ");
			}
			System.out.println();
		}



	}
}
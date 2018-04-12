package gragh;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;


public class makeGragh {
	
	private static Map<String,String> FKtablemap = new HashMap<String,String>();
	private static Map<String[],String> Graghmap = new HashMap<String[],String>();
	private static Map<String,String> Graghmap2 = new HashMap<String,String>();
	private static String[][] maps = new String[100][2]; 
	
	public static Map<String,String> Graph(String[] table,Map<String,String[]> FKmap)
	{	
		Node[] n = new Node[table.length]; //每个实体就是一个节点
		Link[] l = new Link[table.length*table.length]; //每个关系就是一条边
		int[][] relate = new int[table.length][table.length]; //邻接矩阵
		
		for(int i=0;i<table.length;i++) 
		{
			Node nn = new Node(i,table[i],false); //定义点
			n[i] = nn;
			
			for(int j=0;j<table.length;j++)
			{
				relate[i][j] = 0;
			}
		}
		
		FKtablemap = Fliter(FKmap);
		Iterator<String> iter = FKtablemap.keySet().iterator();
		String tableA,tableB,table_FK;
		int A_num = 0,B_num = 0,l_length = 0;
		while(iter.hasNext()) //根据外键关系记录边：序号、起点、终点、外键关联
		{
			String k = iter.next();
			String v = FKtablemap.get(k);
			String[] A = k.split(",");
			tableA = A[0];
			String[] B = v.split(",");
			tableB = B[0];
			for(int i=0;i<table.length;i++)
			{
				if(n[i].gettablename().equals(tableA)) 
					A_num = n[i].getnumber();
				if(n[i].gettablename().equals(tableB))
					B_num = n[i].getnumber();
			}
			
			if(A.length > 2)
			{
				table_FK = A[1] + "=" + B[1];
				for(int i=2;i<A.length;i++)
				{
					table_FK += " and " + A[i] + "=" + B[i];
				}
			}
			else
			    table_FK = A[1] + "=" + B[1];
			
			Link ll = new Link(l_length+1,A_num,B_num,table_FK);
			l[l_length++] = ll;
			relate[A_num][B_num] = l_length; //邻接矩阵记录的是边的名字
			relate[B_num][A_num] = l_length;
		}
		
		/*for(int i=0;i<l_length;i++)
		{
			System.out.println(l[i].getnumber()+": from: "+ l[i].getfromtableNum() + " ,To: "
					+l[i].getToTableNum()+" ,FK: "+l[i].gettableFK());
		}		
		for(int i=0;i<table.length;i++)
		{
			for(int j=0;j<table.length;j++)				
			{
				System.out.print(relate[i][j]+" ");
			}
			System.out.println(" ");
		}
		System.out.println("***************************************");*/
		int maplength = BFS(table.length,l_length,n,l,relate);
		Distinct(Graghmap);
		Iterator<String[]> iter3 = Graghmap.keySet().iterator();
		String[] tables;
		while(iter3.hasNext())
		{
			tables = iter3.next();
			if(tables.length==3)
			{
				String t=tables[0]+","+tables[1]+","+tables[2];
				for(int e=0;e<maplength;e++)
				{
					if(t.equals(maps[e][0]))
					{
						Graghmap2.put(t, maps[e][1]);
						break;
					}
				}
			}
			if(tables.length==2)
			{
				String t=tables[0]+","+tables[1];
				Graghmap2.put(t, Graghmap.get(tables));
			}
		}
		Iterator<String> iter4 = Graghmap2.keySet().iterator();
		while(iter4.hasNext())
		{
			String ta = iter4.next();
	        System.out.print(ta+",");
			System.out.println(Graghmap2.get(ta));
		}
		return Graghmap2;
	}

	public static Map<String, String> Fliter(Map<String, String[]> FKmap) 
	{ //将两个实体间的多个边融合为一条边
		Iterator<String> iter = FKmap.keySet().iterator();
		String k,tableA,tableB;
		String[] v = null;
		String[] exist = new String[FKmap.size()+1];
		int ex=0;
		while(iter.hasNext())
		{
			k = iter.next();
			v = FKmap.get(k);
			for(int i=0;i<v.length;)
			{
				String s1 = v[i++];
				String s2 = v[i++];
				String s3 = v[i++];
				String addA = "";
				String addB = "";
				int j=0;
				while(exist[j] != null)
				{
					String[] ab = exist[j].split(",");
					if((k.equals(ab[0]) && s1.equals(ab[2])) || (k.equals(ab[2]) && s1.equals(ab[0])))
					{
						FKtablemap.remove(ab[0]+","+ab[1]);
						addA = "," + ab[1];
						addB = "," + ab[3];
					}
					j++;
				}
				tableB=k+"," +s3+addB;
				tableA=s1+","+s2+addA;
				FKtablemap.put(tableA, tableB);
				exist[ex]=tableA+","+tableB;
				ex++;
			}
		}
		return FKtablemap;
	}
	
	public static Map<String[],String> Distinct(Map<String[],String> Graphmap)
	{//去重
		Iterator<String[]> iter = Graghmap.keySet().iterator();
		ArrayList<String[]> al = new ArrayList<String[]>();
		String[] tables;
		int j=1;
		while(iter.hasNext())
		{
			//System.out.println("j: "+j++);
			tables = iter.next();
			/*System.out.print("new: ");
			for(String tt:tables)
				System.out.print(tt+",");
			System.out.println("");*/
			Iterator<String[]> iter2 = al.iterator();
			while(iter2.hasNext())
			{
				String[] a = iter2.next();
				/*System.out.print("old: ");
				for(String tt:a)
					System.out.print(tt+",");
				System.out.println("");*/
				if(a.length==2 && tables.length==2)
				{
					if((tables[0].equals(a[1]) && tables[1].equals(a[0]))||(tables[0].equals(a[0]) && tables[1].equals(a[1])))
					{
						iter.remove();
						break;
					}
				}
				if(a.length==3 && tables.length==3)
				{
					if((tables[1].equals(a[2]) && tables[2].equals(a[1]) && tables[0].equals(a[0]))
					||(tables[1].equals(a[0]) && tables[0].equals(a[1]) && tables[2].equals(a[2]))
					||(tables[0].equals(a[2]) && tables[1].equals(a[0]) && tables[2].equals(a[1]))
					||(tables[0].equals(a[2]) && tables[2].equals(a[0]) && tables[1].equals(a[1]))
					||(tables[0].equals(a[1]) && tables[1].equals(a[2]) && tables[2].equals(a[0])))
					{
						iter.remove();
						break;
					}
				}
			}
		    al.add(tables);
		}
		return Graghmap;
	}
	public static int BFS(int t_length,int l_length,Node[] n,Link[] l,int[][] relate)
	{
		//广度优先遍历
		int maplength=0;
		Queue<Integer> q = new LinkedList<Integer>();
		for(int i=0;i<t_length;i++)
		{
			String[] g = new String[3];
			g[0] = n[i].gettablename();
			String gk1 = "",gk2 = "";
			int round = 1;
			int[] parent = new int[3];
			for(int u=0;u<3;u++)
			{
				parent[u] = -1;
			}
			for(int u=0;u<t_length;u++)
			{
				n[u].setvisited(false);
			}
			if(n[i].getvisited() == false) //未被访问过
			{
				q.add(n[i].getnumber());
				n[i].setvisited(true);
				//System.out.print(n[i].getnumber()+n[i].gettablename()+",");
				while(!q.isEmpty()) //假设队列非空
				{
					int j = (Integer)q.remove().intValue(); //删除队列的顶点
					//System.out.println("delete"+j);
					
					int[] k = new int[t_length]; int k_length=0;
					for(int h=0;h<t_length;h++)
					{
						if(relate[j][h]>0 && n[h].getvisited()==false) //获得邻接与上一节点的节点们
						{
							k[k_length++] = h;
							//System.out.println("relete"+h);
						}
					}
					
					for(int h=0;h<k_length;h++)
					{
						q.add(n[k[h]].getnumber());
						n[k[h]].setvisited(true);
						//System.out.print(n[k[h]].getnumber()+n[k[h]].gettablename()+",");
						if(j==i)
						{
							parent[h] = k[h];
							g[1] = n[k[h]].gettablename();
							int r = relate[j][k[h]]; 
							int d;
							for(d=0;d<l_length;d++)
							{
								if(l[d].getnumber() == r)
									break;
							}
							gk1 = l[d].gettableFK();
							Graghmap.put(g,gk1);							
							//System.out.println(g[0]+","+g[1]+","+gk1);
							maps[maplength][0] = g[0]+","+g[1];
							maps[maplength][1] = gk1;
							maplength++;
						}
						else if(j==parent[0] || j==parent[1] || j==parent[2])
						{
							g[1] = n[j].gettablename();
							int r = relate[i][j]; 
							int d;
							for(d=0;d<l_length;d++)
							{
								if(l[d].getnumber() == r)
									break;
							}
							gk1 = l[d].gettableFK();
							
							g[2] = n[k[h]].gettablename();
							r = relate[j][k[h]]; 
							for(d=0;d<l_length;d++)
							{
								if(l[d].getnumber() == r)
									break;
							}
							gk2 = gk1+ " and "+l[d].gettableFK();
							String[] gg = new String[2];
							gg[0] = g[0];
							gg[1] = g[1];
							Graghmap.put(gg,gk1);
							String[] ggg = new String[3];
							ggg[0] = g[0];
							ggg[1] = g[1];
							ggg[2] = g[2];
							Graghmap.put(ggg,gk2);							
							maps[maplength][0] = g[0]+","+g[1]+","+g[2];
							maps[maplength][1] = gk2;
							maplength++;
						}
					}
					round++;
				}
			}
		}
		return maplength;
	}
}

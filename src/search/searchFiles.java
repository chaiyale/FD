package search;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.Document;

/**
 * Servlet implementation class searchFiles
 */
public class searchFiles extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public searchFiles() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String keyword = request.getParameter("keyword");
		//PrintWriter out = response.getWriter();
		//out.println("What you are looking for is:"+ keyword);
		//out.flush();	
		DataIndex di = new DataIndex();
		Map<String,ArrayList<String>> Sqi = new HashMap<String,ArrayList<String>>();
		ArrayList<String> FormTerm = new ArrayList<String>();
		if(di.execute(request))
		{
			FormTerm = di.getFormTerm(); //获得表单词汇
			//out.print("The FormTerms are:");
			//for(String tmp:FormTerm)
				//out.print(tmp+" ");
			Sqi = di.getSqi(); //获得关键字所属的表
			/*Iterator<String> iter = Sqi.keySet().iterator();
			String k;
			while(iter.hasNext())
			{
				k = iter.next();
				ArrayList<String> ar = Sqi.get(k);
				System.out.print("k:" + k +"   v:");
				for(String tmp:ar)
					System.out.print(tmp+" ");
				System.out.println("");
			}*/
			Enumerate en = new Enumerate(Sqi);
			ArrayList<String[]> Sq = en.getSq(); //从每个查询的盒子中拿出一个，作为查询
			/*Iterator<String[]> iter1 = Sq.iterator();
			while(iter1.hasNext())
			{
				String[] kk = iter1.next();
				for(String tmp:kk)
					System.out.print(tmp+ " ");
				System.out.println("");
			}*/
			FormIndex fi = new FormIndex();
			ArrayList<Document> docs = fi.getDocs(Sq); //所有查询做AND查询，结果取并集
			request.setAttribute("Docs", docs);
			String haha = "search/search.jsp";
			request.getRequestDispatcher(haha).forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doGet(request, response);
	}

}

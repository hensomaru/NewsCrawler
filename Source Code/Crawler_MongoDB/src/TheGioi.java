import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class TheGioi implements Crawler {
	@Override
	public void craw(){
		try{
			Server.doc = Jsoup.connect("http://vnexpress.net/tin-tuc/the-gioi").get();
			//get all links and recursively call the processPage method
			Elements questions = Server.doc.select("a[href]");
			for(Element link: questions){
				if(link.attr("href").contains("vnexpress.net")){
					Document doc2 = Jsoup.connect(link.attr("abs:href")).ignoreHttpErrors(true).get();
					//if(!doc2.body().getElementsByAttributeValueContaining("src", "media.bongda.com.vn/files").attr("src").isEmpty()){
						DBObject links = new BasicDBObject().append("Link", link.attr("abs:href"))
								 							.append("Title", doc2.title())
								 							.append("Type", "The Gioi")
								 							.append("Images",doc2.body().getElementsByAttributeValueContaining("src","http://img.f29.vnecdn.net/" ).attr("src"));

						BasicDBObject andQuery = new BasicDBObject();
						List<BasicDBObject> object = new ArrayList<BasicDBObject>();
						object.add(new BasicDBObject("Type", "The Gioi"));
						object.add(new BasicDBObject("Link", link.attr("abs:href")));
						andQuery.put("$and", object);
						
						DBCursor cursor = Server.col.find(andQuery);
						if(cursor.count()==0){
							Server.col.insert(links);
							System.out.println(link.attr("abs:href"));
						}
					//}
				}
			}
		}catch(IOException e){
			System.out.println("Craw The Gioi news finished !");
		}
	}
}

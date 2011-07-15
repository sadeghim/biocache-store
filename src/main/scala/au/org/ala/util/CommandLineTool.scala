package au.org.ala.util

object CommandLineTool {

  def main(args: Array[String]): Unit = {  
      
      //need a mechanism to delegate to other tools
      
      var tool:Option[String] = None
      
      val parser = new OptionParser("load flickr resource") {
         opt("t", "Select the tool to use", {v: String => tool = Some(v)})
      }
      
      if(parser.parse(args)){
    	  println("""=================================""")
    	  println("""=== biocache commandline tool """)
    	  println("""=================================""")
    	  
    	  println("""1) harvest resource """)
    	  println("""2) process resource """)
    	  println("""3) index resource / all / recently modified""")
      }
  }
}
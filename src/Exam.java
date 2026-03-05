public  class Exam {
    private String regNo;
    private String name;
    private Integer cat1;
    private Integer cat2;
    private Integer main;


    public Exam(String regNo, String name,Integer cat1,Integer cat2,Integer  main) {
        this.regNo = regNo;
        this.name = name;
        this.main=main;
        this.cat1=cat1;
        this.cat2=cat2;
    }

   

    public String getRegNo() { return regNo; }
    public String getName() { return name; }
    public Integer  getCat1(){
        return cat1;
       
    }
     public Integer  getCat2(){
            return cat2;
            
        }
      
         public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

     public void setName(String name) {
         this.name = name;
     }
     public void  setCat2(Integer  cat2){
      this.cat2= cat2;
     }

     public void setCat1(Integer  cat1) {
         this.cat1 = cat1;
     }


     public void setMain(Integer  main) {
         this.main = main;
     }

         public Integer getMain(){
            return main;
            
        }
}
 
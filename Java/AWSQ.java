import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class AWSQ {
    static class Question{

        // Contains all the tags for the question
        String[] tags;

        // Contains the string Question
        String question;

        // Contains the Answer of the Question
        String answer;

        // Contains all the options for the question
        String[] options;

        // Contains the Explanation of the Question
        String explanation;

        // Contains all the times the question has been asked
        int total;

        // Contains all the times the question has been correct
        int correct;

        // Contains all the times the question has been wrong
        int wrong;

        // Contains the tally of the question
        int tally;

        Question(String[]tags,String question,String answer,String[]options,
                 String explanation, int total, int correct, int wrong, int tally)
        {
            this.tags = tags;
            this.question = question;
            this.answer = answer;
            this.options = options;
            this.explanation = explanation;
            this.total = total;
            this.correct = correct;
            this.wrong = wrong;
            this.tally = tally;
        }

        int tallyUp(){
            tally+=1;
            if (tally >= 3){
                tally = 3;
            }
            return tally;
        }

        int tallyDown(){
            tally-=1;
            if (tally<0){
                tally = 0;
            }
            return tally;
        }
        int asked(){
            total+=1;
            return total;
        }
        int correcto(){
            correct+=1;
            asked();
            tallyUp();
            return tally;
        }

        int wrongo(){
            wrong+=1;
            asked();
            tallyDown();
            return tally;
        }


        void myContents(){
            for(String e: tags){
                System.out.println(e);
            }
            System.out.println(question);
            System.out.println(answer);
            System.out.println(explanation);
            for(String e: options){
                System.out.println(e);
            }
            System.out.println(String.valueOf(total));
            System.out.println(String.valueOf(correct));
            System.out.println(String.valueOf(wrong));
            System.out.println(String.valueOf(tally));

        }
        String saveString(){
            String save = "";
            int thingLength = tags.length;
            int i = 0;
            String parse = "";

            parse = "!";
            // Start off with the tags
            while(i<thingLength){
                save += tags[i];
                i++;
                if(i!=thingLength){
                    save+=parse;
                }
            }

            save+="_";
            //Question
            save += question;

            save+="_";

            //Answer
            save += "$" + answer;

            save+="_";

            //Options
            parse = ">";
            i = 0;
            thingLength = options.length;
            // Start off with the tags
            while(i<thingLength){

                save += options[i];
                i++;
                if (i != thingLength) {
                    save += parse;
                }

            }

            save+="_";

            //Explanation
            save += "@" + explanation;

            save+="_";

            //Total
            save += "#" + String.valueOf(total);

            save+="_";

            //Correct
            save += "+" + String.valueOf(correct);

            save+="_";

            //Wrong
            save += "-" + String.valueOf(wrong);

            save+="_";

            //Tally
            save += "%" + String.valueOf(tally) + "\n";
            return save;
        }
    }
    static class TheTags{
        // The Strings of the file
        String tagName;

        // Contains all the questions in a list
        Question[] tagQuestions = new Question[200];
        int newQIndex = 0;

        // Tags Completed List Float
        float tagCompletion = 0.0f;

        // Tags Number of Questions Correct
        int tagNumberOfQuestionsCorrect = 0;

        // Tags Number of Questions Wrong
        int tagNumberOfQuestionsWrong = 0;

        // Tags Bucket of questions that can be asked
        Question[] tagBucket = new Question[200];
        int tagBucketIndex = 0;
        int[] bucketRandomness = new int [200];

        // Tags Bucket of questions that have been answered correctly
        Question[] tagGoodBucket = new Question[200];
        int tagGoodBucketIndex = 0;

        // Tags Bucket of questions that have been answered wrong
        Question[] tagBadBucket = new Question[200];
        int tagBadBucketIndex = 0;

        // Tally of the tag
        int tagTally = 0;

        TheTags(String tagName){this.tagName = tagName;}

        void tagNewQuestion(Question newQ){
            tagQuestions[newQIndex] = newQ;
            newQIndex++;
        }

        void cleaningThisTag(){
            // Tags bucket
            // tag doneBucket
            tagGoodBucketIndex = 0;
            tagBadBucketIndex = 0;
            tagBucketIndex=0;
            int i_loop = 0;
            float someTally = 0;
            while(i_loop<newQIndex){
                Question q = tagQuestions[i_loop];
                tagNumberOfQuestionsCorrect+=q.correct;
                tagNumberOfQuestionsWrong+=q.wrong;
                tagTally+=q.tally;
                someTally+=q.tally;
                if (q.tally > 2){
                    tagGoodBucket[tagGoodBucketIndex] = q;
                    tagGoodBucketIndex++;
                }else {
                    bucketRandomness[tagBucketIndex] = tagBucketIndex;
                    tagBadBucket[tagBadBucketIndex] = q;
                    tagBucket[tagBadBucketIndex] = q;
                    tagBucketIndex++;
                    tagBadBucketIndex++;
                }
                i_loop++;
            }
            tagCompletion = someTally / (newQIndex * 3) * 100.0f;
            //System.out.println("The completion for this is " + String.valueOf(tagCompletion));
            stackShuffle(tagBucketIndex);
        }

        void allInBucket(){
            cleaningThisTag();
            int i_loop = 0;
            while(tagGoodBucketIndex > 0){
                if (tagGoodBucket[tagGoodBucketIndex] != null){
                    tagBucket[tagBucketIndex] = tagGoodBucket[tagGoodBucketIndex];
                    tagBucketIndex++;
                }
                tagGoodBucketIndex--;
            }
            stackShuffle(tagBucketIndex);
        }



        void stackShuffle(int limit){
            Random numb = new Random();
            int j = 0;
            while (j<limit){
                bucketRandomness[j] = j;
                j++;
            }

            for (int i = 0; i < limit; i++) {
                int r1 = numb.nextInt(limit);
                int r2 = numb.nextInt(limit);
                int temp1 = bucketRandomness[r1];
                int temp2 = bucketRandomness[r2];
                bucketRandomness[r2] = temp1;
                bucketRandomness[r1] = temp2;
                //System.out.println(String.valueOf(bucketRandomness[i]));
            }




            //System.out.println(Arrays.toString(array));
        }

        Question orderUp(){
            tagBucketIndex--;
            if(tagBucketIndex < 0){
                return tagBucket[0];
            }
            //System.out.println(String.valueOf(tagBadBucketIndex+1));
            return tagBucket[bucketRandomness[tagBucketIndex]];
        }

        void orderTest(){
            //randomTest();
            stackShuffle(tagBucketIndex);
            while(tagBucketIndex > 0){
                tagBucketIndex--;
                System.out.println(String.valueOf(bucketRandomness[tagBucketIndex]));
                System.out.println(tagBucket[bucketRandomness[tagBucketIndex]].question);;
            }
        }

    }

    static class TheFile{
        // The Strings of the file
        String obj;

        // Contains all the tags with their indexes
        TheTags[] tags = new TheTags[100];
        int tagLimit = 0;

        Question[] qs = new Question[200];
        int questionLimit = 0;

        TheFile(String obj){
            this.obj = obj;
        }


        void intializeTheFile(){
            allThrough();

            int i_loop= 0;
            while(i_loop<tagLimit){
                tags[i_loop].cleaningThisTag();
                //tags[i_loop].orderTest();
                i_loop++;
            }
            //saveSystem();
            theWelcome(0);
        }

        void allThrough(){

            try {
                File saveData = new File("src/awsd.txt");
                Scanner myReader = new Scanner(saveData);
                //System.out.println("Saved");
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    savedRetrieval(data);
                }
                myReader.close();
            }catch(FileNotFoundException e){
                String [] qBelow = theQ().split("\n");
                for (String data: qBelow) {
                    savedRetrieval(data);
                }
                String [] qBeloww = theQQ().split("\n");
                for (String dataa: qBeloww) {
                    savedRetrieval(dataa);
                }
                //unSaved();
            }
        }

        void savedRetrieval(String longstring){
            // New order
            //System.out.println(longstring);
            int dataIndex = 0;
            String[] dataStack = new String[30];
            int dataAnswer = 0;
            int dataTags = 0;
            int dataOptions = 0;
            int dataQuestion =0;
            int dataExplanation = 0;
            int dataCorrect = 0;
            int dataWrong = 0;
            int dataTally = 0;
            int i_loop = 0;
            String[] spl = longstring.split("_");
            for (String piece:spl) {
                if (piece.contains("!")) {
                    for (String s : piece.split("!")) {
                        dataStack[dataIndex] = s;
                        dataIndex++;
                        dataTags++;
                    }
                }
                if (piece.contains("?")) {
                    dataQuestion = dataIndex;
                    dataStack[dataIndex] = piece;
                    dataIndex++;
                }

                if (piece.contains("$")) {
                    dataAnswer = dataIndex;
                    dataStack[dataIndex] = piece.split("\\$")[1];
                    dataIndex++;
                }

                if (piece.contains(">")) {
                    for (String s : piece.split(">")) {
                        dataStack[dataIndex] = s;
                        dataIndex++;
                        dataOptions++;
                    }
                }

                if (piece.contains("@")) {
                    dataExplanation = dataIndex;
                    dataStack[dataIndex] = piece.split("@")[1];
                    dataIndex++;
                }

                if (piece.contains("+")) {
                    dataCorrect = Integer.valueOf(piece.split("\\+")[1]);
                }

                if (piece.contains("-")) {
                    dataWrong = Integer.valueOf(piece.split("-")[1]);
                }
                if (piece.contains("%")) {
                    dataTally = Integer.valueOf(piece.split("%")[1]);
                }
            }
            i_loop = 0;
            String[] tagsq = new String[dataTags];
            while (i_loop < dataTags) {
                tagsq[i_loop] = dataStack[i_loop];
                i_loop++;
            }
            String questionq = dataStack[dataQuestion];
            String answerq = dataStack[dataAnswer];

            i_loop = dataAnswer;
            String[] optionsq = new String[dataOptions];
            int j_loop = 0;
            //while (i_loop < dataOptions + dataAnswer - 1) {
            while (i_loop < dataOptions + dataAnswer) {
                optionsq[j_loop] = dataStack[i_loop+1];
                i_loop++;
                j_loop++;
            }
            String explantionq = dataStack[dataExplanation];

            Question newQ = new Question(tagsq, questionq, answerq, optionsq, explantionq, dataCorrect+dataWrong, dataCorrect, dataWrong, dataTally);
            qs[questionLimit] = newQ;
            questionLimit++;
            //newQ.myContents();
            for (String sometag : tagsq) {
                taggingEm(sometag, newQ);
            }
        }

        void unSaved(){
            //System.out.println("Unsaved");
            int dataIndex = 0;
            String[] dataStack = new String[30];
            int dataAnswer = 0;
            int dataTags = 0;
            int dataOptions = 0;
            String [] qBelow = theQ().split("\n");
            for (String data: qBelow){
                //System.out.println(data);
                if (data.contains("?")){
                    dataIndex = 0;
                    dataStack[dataIndex] = data.split("_")[data.split("_").length - 1];
                    dataIndex++;
                    dataTags = data.split("_").length-1;
                    String[] tagsoq = new String[dataTags];
                    tagsoq = myStringSplice(tagsoq,data.split("_"),0,dataTags);
                    for (String e : tagsoq){
                        dataStack[dataIndex]=e;
                        dataIndex++;
                    }
                }
                if (data.contains("$")){
                    dataAnswer = dataIndex;
                    dataStack[dataIndex] = data.split("\\$")[data.split("\\$").length - 1];
                    dataIndex++;

                }

                if (data.contains(">")){
                    dataStack[dataIndex] = data;
                    dataIndex++;
                    dataOptions++;
                }
                if (data.contains("@")) {
                    String questionq = dataStack[0];
                    String answerq = dataStack[dataAnswer];
                    String explantionq = data.split("@")[data.split("@").length - 1];
                    String[] tagsq = new String[dataTags];
                    dataOptions+=1;
                    String[] optionq = new String[dataOptions];
                    int tagsqi = 0;
                    int optionqi = 0;
                    while (tagsqi < dataIndex){
                        String s = dataStack[tagsqi];
                        if (s.contains(">")){
                            optionq[optionqi] = s.split(">")[1];
                            optionqi++;
                        }
                        tagsqi++;

                    }
                    optionq[dataOptions-1] = answerq;
                    tagsqi = 1;
                    optionqi = 0;
                    while (optionqi < dataTags){
                        tagsq[optionqi] = dataStack[tagsqi];
                        tagsqi++;
                        optionqi++;
                    }

                    Question newQ = new Question(tagsq, questionq, answerq, optionq, explantionq, 0, 0, 0, 0);
                    qs[questionLimit]=newQ;
                    questionLimit++;
                    //newQ.myContents();
                    for(String s: tagsq){
                        taggingEm(s,newQ);
                    }
                    dataAnswer = 0;
                    dataTags = 0;
                    dataOptions = 0;

                }
            }
        }

        void saveSystem(){
            try {
                File myFoo = new File("src/awsd.txt");
                FileOutputStream fooStream = new FileOutputStream(myFoo, false);
                int i_loop = 0;
                while(i_loop<questionLimit){
                    byte[] myBytes = qs[i_loop].saveString().getBytes();
                    fooStream.write(myBytes);
                    i_loop++;
                }
                //byte[] myBytes = "New Contents\n".getBytes();
                //fooStream.write(myBytes);
                fooStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Not doint it");
            }
        }

        void taggingEm(String tagName, Question newQ){
            int i_loop = 0;
            int tagotago = tagLimit;

            while (i_loop<tags.length){
                if (tags[i_loop] == null){
                    tags[i_loop] = new TheTags(tagName);
                    tags[i_loop].tagNewQuestion(newQ);
                    tagLimit++;
                    return;
                }
                String ns = tags[i_loop].tagName;
                String ss = tagName;
                if (ns.equals(ss)){
                    tags[i_loop].tagNewQuestion(newQ);
                    i_loop+=10000000;
                }
                i_loop++;
            }

        }


        String[] myStringSplice(String[] toString,String[] fromString, int fromIndex, int toIndex){
            int i_loop = 0;
            int j_loop = fromIndex;
            while(i_loop<toString.length){
                if(toString[i_loop] == null && j_loop<toIndex){
                    toString[i_loop] = fromString[j_loop];
                    j_loop++;
                }
                i_loop++;
            }
            return toString;
        }


        void theWelcome(int type){
            if (type == 0){
                System.out.println("\n\nWelcome to AWS Training\n\n");
            }else{
                System.out.println("\nWelcome Back\n\n");
            }
            int i = 0;
            String[] menuPick = new String[tagLimit];
            while(i<tagLimit){
                tags[i].cleaningThisTag();
                menuPick[i] = Integer.toString(i);
                if (tags[i].tagCompletion > 99){
                    System.out.println(String.valueOf(i) + " " + tags[i].tagName + "\t" + "100"+ "%");

                }else {
                    System.out.println(String.valueOf(i) + " " + tags[i].tagName + "\t" + String.valueOf(tags[i].tagCompletion).split("")[0] + String.valueOf(tags[i].tagCompletion).split("")[1] + "%");
                }
                i++;
            }
            System.out.println("\n");


            if (type == 0){
                System.out.println("What topic would you like to work on today?\n");
            }else{
                System.out.println("What topic would you like to work now?\n");
            }
            System.out.println("Enter a number corresponding to the topic you want or Enter anything else to exit");
            Scanner myinput = new Scanner(System.in);
            String myans = myinput.nextLine();
            i = 0;
            int chosen = -200;
            while(i<tagLimit){
                if (menuPick[i].equals(myans)){
                    chosen = i;
                    i+=10000000;
                }
                i++;
            }
            if (chosen < 0){
                return;
            }else{
                hotSeat(tags[chosen]);
            }
        }

        void hotSeat(TheTags tag){
            System.out.println("How many questions would you like for " + tag.tagName + " ?");
            Scanner myinput = new Scanner(System.in);
            String myans = myinput.nextLine();
            int qLimit = Integer.parseInt(myans);
            System.out.println("\n" + tag.tagName + " it is then");
            System.out.println("Okay here we go\n\n");
            if (qLimit > tag.tagBucketIndex){
                qLimit = tag.tagBucketIndex;
            }
            if(qLimit<1 || qLimit >200){
                theWelcome(1);
                return;
            }
            int i_loop = 0;
            while (i_loop < tag.tagBucketIndex && i_loop < qLimit){
                askingAWS(tag.orderUp());
                saveSystem();
                i_loop++;
            }
            tag.cleaningThisTag();
            if(i_loop < tag.tagBucketIndex){
                afterOptions(tag);
            }else{
                afterOptions(tag);
            }
            return;
        }

        void afterOptions(TheTags tag){
            String[] givenOptions = {"Review all the questions on this topic again", "Retry questions which were wrong", "Go to the Main Menu"};
            String myans = makeADecision("It looks like you have answered all the questions",givenOptions);
            if (myans.equals("1")){
                tag.allInBucket();
                hotSeat(tag);

            }else if(myans.equals("2")){
                // Shuffle the bucket and reset the index of the bucket
                // recall the hot seat
                tag.cleaningThisTag();
                hotSeat(tag);

            }else{
                // leave
                theWelcome(1);
                return;
            }

        }

        String makeADecision(String prompt, String[] givenOptions){
            System.out.println(prompt);
            String[] optionNumbers = {"1","2","3"};
            int i_loop = 0;
            while (i_loop< givenOptions.length){
                System.out.println(optionNumbers[i_loop] + " " + givenOptions[i_loop]);
                i_loop++;
            }
            System.out.println("Choose a number corresponding to an option");
            Scanner myinput = new Scanner(System.in);
            String myans = myinput.nextLine();
            return myans;
        }

        String[] optionalRandomness(String[] originalList)
        {
            String[] randomizedList = new String[originalList.length];
            Random numb = new Random();
            int i = 0;
            int filled = 0;
            int random = 0;
            int j = 0;
            int add = -1;
            while(i<100000 && filled < originalList.length){
                random = numb.nextInt(originalList.length);
                j = 0;
                add = 1;
                while (j< originalList.length){
                    if (originalList[random] == randomizedList[j]){
                        add = -1;
                    }
                    j++;
                }
                if (add == 1){
                    randomizedList[filled] = originalList[random];
                    filled++;
                }
                i++;
            }

            return randomizedList;
        }


        void askingAWS(Question q){
            String myans = "";
            System.out.println("\n\n");
            long startTime = System.nanoTime()/1000000000;
            //System.out.println(q.question);
            for (String qque: q.question.split("\\.")){
                System.out.println(qque);
            }
            String[] opto = optionalRandomness(q.options);
            String[] optListUpper = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P"};
            String[] optListLower = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p"};
            String[] optListNumber= {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16"};
            double questionTimeLimit = (q.question.length() + q.answer.length()) * 0.2;
            int i = 0;
            int answerIndex = 0;
            while (i< opto.length){
                System.out.println(optListUpper[i] + " " + opto[i]);
                if (opto[i].equals(q.answer)){
                    answerIndex = i;
                }
                i++;
            }
            System.out.println("\n");
            System.out.println("You have exactly " + String.valueOf(questionTimeLimit) + " seconds to answer.");
            System.out.println("\n" + "What is your answer = ");
            Scanner myinput = new Scanner(System.in);
            myans = myinput.nextLine();
            long endtTime = System.nanoTime()/1000000000;


            long timeDifference = endtTime - startTime;
            if (myans.equals(q.answer) || myans.equals(optListUpper[answerIndex]) || myans.equals(optListLower[answerIndex]) || myans.equals(optListNumber[answerIndex])){


                if (timeDifference < questionTimeLimit){
                    q.correcto();
                    System.out.println("\n You are Correct :)\n");
                    System.out.println("Saving Results. Pease wait\n\n");
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    System.out.println("However you took too long to answer so it will count as incorrect");
                    System.out.println("Your Response Time was:");
                    System.out.println(String.valueOf(timeDifference) + "  seconds out of the given " + String.valueOf(questionTimeLimit) + " seconds\n\n");
                    q.wrongo();
                    System.out.println("Saving Results\n\n");
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }else{
                //System.out.println(String.valueOf(timeDifference));
                q.wrongo();
                System.out.println("\nYou are Wrong :(\n");
                //System.out.println(q.explanation + "\n\n");
                for (String eexp: q.explanation.split("\\.")){
                    System.out.println(eexp);
                }

                System.out.println("\nSaving Results\n\n");
                questionTimeLimit = (q.explanation.length()) * 0.05;
                try {
                    TimeUnit.SECONDS.sleep((int)questionTimeLimit);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        //System.out.println("Hello There");
        TheFile awsdDrive = new TheFile("Hello");
        awsdDrive.intializeTheFile();

    }
    static String theQ() {
        String questionings = "Console!IAM_What does IAM stand for?_$Identity and Access Management_Information and Access Management>Information and Access Membership>Identity and Access Membership>Identity And Membership>Identity and Access Management_@IAM stands for Identity and Access Management_#0_+0_-0_%0\n" +
                "Console!IAM_What are some of the features of IAM?_$All of these_Centralized control of your AWS account>Shared access to your AWS account>Granular permissions or system privileges>Identity Federation such as Facebook LinkedIN>None of these>All of these_@These are all great features of IAM_#0_+0_-0_%0\n" +
                "Console!IAM_What are some features of IAM?_$All of these_Multi factor Authentication or MFA>Provides temporary access for users or devices and services where necessary>Allows you to set up your own password rotation policy>Integrates with many different AWS Services>None of these>All of these_@These are all great features of IAM_#0_+0_-0_%0\n" +
                "Console!IAM_Who can an IAM be created for?_$Anyone_Employees>Managers>Admins>Anyone_@An AMI can be created for anyone_#0_+0_-0_%0\n" +
                "Console!IAM_There can be a group of IAM accounts that share the same permissions?_$True_False>True_@This is true there can be a group of IAM accounts that share the same permissions_#0_+0_-0_%0\n" +
                "Console!IAM_What is the format of IAM policy documents?_$JSON_XML>YML>HTML>JSON_@Currently JSON is the only supported format for IAM. In the past there used to be other options_#0_+0_-0_%0\n" +
                "Console!IAM_What does IAM allow an admin to do?_$allows the admin to manage users and their level of access to the AWS console_allows the admin to manage other admins and their level of access to the AWS console>allows the admin to manage users and their level of access to the AWS SDK>allows the admin to manage other admins and their level of access to the AWS CLI>allows the admin to manage users and their level of access to the AWS console_@allows the admin to manage users and their level of access to the AWS console_#0_+0_-0_%0\n" +
                "Console!IAM_An IAM policy is made up of documents called?_$Policy Documents_Agreement Documents>Permission Documents>AWS Permission Documents>Policy Documents_@Policy Documents is what IAM policies are called_#0_+0_-0_%0\n" +
                "Console!IAM_You can create roles and assign them to AWS resources?_$True_False>True_@Roles are created and assigned to AWS resources_#0_+0_-0_%0\n" +
                "Console!IAM_The regionality of IAM is?_$Universal_Region Specific>Universal_@IAMs are not tied to one region they can be assessed from all regions_#0_+0_-0_%0\n" +
                "Console!Account_What is an AWS root account?_$It is an account with complete admin access_It is an account with access only given by created policies>It is an account that an IAM user can create>It is an account for EC2>It is an account for S3>It is an account with complete admin access_@AWS Root Accounts have complete admin access_#0_+0_-0_%0\n" +
                "Console!Account_An AWS Root Account is created when?_$AWS account is first created_an IAM user authorizes it>At the start of the first EC2 instance>Only in the CLI>Only by SDK>AWS account is first created_@An AWS Root Account is created when an AWS account is first created_#0_+0_-0_%0\n" +
                "Console!IAM_Newly created IAM users have?_$no permissions unless added_some permissions services such as EC2 and S3>all permissions unless removed by admin>all permissions unless removed by policies>no permissions unless added_@New IAM users have no permissions unless added_#0_+0_-0_%0\n" +
                "Console!IAM_Newly created IAM users are assigned?_$Access Key ID and Secret Access Keys_Access Key ID>Secret Access Keys>Admin Root Keys>Access Key ID and Secret Access Keys_@Newly created IAM users are assigned both Access Key ID and Secret Access Keys_#0_+0_-0_%0\n" +
                "Console!IAM_IAM access keys are?_$None of theses_The same as passwords>A combination of the Secret Access Keys and admin password>The same as Admin password>None of theses_@Access Keys are generated by AWS_#0_+0_-0_%0\n" +
                "Console!IAM_IAM Secret Access Keys are?_$None of theses_The same as passwords>A combination of the Access Keys and admin password>The same as Admin password>None of theses_@Secret Access Keys are generated by AWS_#0_+0_-0_%0\n" +
                "Console!IAM!CLI_You can use Access Keys and Secret Keys to log into?_$All of these_AWS Console>CLI>APIs>All of these_@Access Keys and Secret Keys can be used to log into the AWS Console, CLI and APIs_#0_+0_-0_%0\n" +
                "Console!Account_It is strongly recommended that for your root AWS account to have?_$MFA_Access from all IAM>Access from some IAM>Full access from all IAM>MFA_@MFA or Multi Factor Authentication_#0_+0_-0_%0\n" +
                "Storage!S3_What does S3 stand for?_$Simple Storage Service_Special Storage Service>Sub Storage Service>Several Storage Services>Simple Storage Service_@Simple Storage Service_#0_+0_-0_%0\n" +
                "Storage!S3_S3 storage is?_$Object based_JSON based only>Text based only>SDK based>Object based_@This means that you are allowed to upload you own files with no constraint on type_#0_+0_-0_%0\n" +
                "Storage!S3_A single File in S3 must have a size limit of what?_$5 TB_1 GB>There is no limit>1 ZB>5 GB>5 TB_@The limit for single file that you can upload to S3 is 5TB which is huge_#0_+0_-0_%0\n" +
                "Storage!S3_There is unlimited storage in S3?_$True_False>True_@Thats right S3 has unlimited storage that is if you can afford it_#0_+0_-0_%0\n" +
                "Storage!S3_Files in S3 are stored in?_$Buckets_Hashes>EC2 instances>IAM storage directory>Buckets_@S3 files are stored in buckets_#0_+0_-0_%0\n" +
                "Storage!S3_The namespace of S3 files must?_$be unique_Does not matter you can use any name>have no special characters>have special characters>have numbers>be unique_@All names of S3 files must be unique_#0_+0_-0_%0\n" +
                "Storage!S3_After an S3 file is successfully uploaded what is received?_$HTTP 200 code_A gift from Jeff Bezos>Nothing>HTTP 401>HTTP 400>HTTP 200 code_@HTTP 200 code which is a code of success_#0_+0_-0_%0\n" +
                "Storage!S3_Objects of S3 consist of?_$key, value, metadata, sub resources, version id_Key>Value>Key and Value>Key, value and Metadata>key, value, metadata, sub resources and control number>key, value, metadata, sub resources, version id_@key, value, metadata, sub resources, version id_#0_+0_-0_%0\n" +
                "Storage!S3_S3 guarantees an availability of?_$99 percent_100 percent>80 percent>Depends on how much data is being accessed>99 percent_@S3 guarantees an availability of 99 percent_#0_+0_-0_%0\n" +
                "Cloud!AWS_Which one of the following are signs of a highly available application?_$A failure in one geographic region will trigger an automatic failover to resources in a different region_Applications are protected behind multiple layers of security>Virtualized hypervisor driven systems are deployed as mandated by company policy>A failure in one geographic region will trigger an automatic failover to resources in a different region_@Security and virtualization are both important characteristics of successful cloud workloads but neither will directly impact availability which is critical._#0_+0_-0_%0\n" +
                "Cloud!AWS_Which of the following are signs of a highly available application?_$Spikes in user demand are met through automatic increasing resources._Applications are protected behind multiple layers of security>Virtualized hypervisor driven systems are deployed as mandated by company policy>Spikes in user demand are met through automatic increasing resources._@Security and virtualization are both important characteristics of successful cloud workloads but neither will directly impact availability which is critical._#0_+0_-0_%0\n" +
                "Account!Billing_How does the metered payment model make many benefits of cloud computing possible?_$Full stack applications are possible without the need to invest in capital expenses_Greater application security is now possible>Applications are now highly scalable>Full stack applications are possible without the need to invest in capital expenses_@Capital expenses are greatly reduced with metered payment. Although security and scalability are important they have nothing to do with cost_#0_+0_-0_%0\n" +
                "AWS!Cloud_Which of the following is direct benefits of server virtualization?_$Efficient high density use of resources _Greater application security>Elastic application designs>Efficient high density use of resources _@Security and elasticity are important but are not directly related to server virtualization_#0_+0_-0_%0\n" +
                "Account!Billing_Which of the following benefits of cloud computing is a result of the metered payment model?_$Experiments with multiple configurations options are now cost effective_Greater application security is now possible>Applications are now highly scalable>Experiments with multiple configurations options are now cost effective_@Capital expenses are greatly reduced with metered payment. Although security and scalability are important they have nothing to do with cost_#0_+0_-0_%0\n" +
                "AWS!Cloud_Which of the following is another direct benefits of server virtualization?_$Fast resources provisioning and launching_Greater application security>Elastic application designs>Fast resources provisioning and launching_@Security and elasticity are important but are not directly related to server virtualization_#0_+0_-0_%0\n" +
                "AWS!Cloud_What is a hypervisor?_$Software used to administrate virtualized resources run on physical infrastructure_Hardware device used to provide an interface between storage and compute modules>Hardware device used to provide an interface between networking and compute modules>Software used to log and monitor virtualized operations>Software used to administrate virtualized resources run on physical infrastructure_@A hypervisor is a software not hardware which administrates virtualization operations_#0_+0_-0_%0\n" +
                "AWS!Cloud_Which of the following best describes server virtualization?_$Logically partitioning physical compute and storage devices into multiple smaller virtual devices_Sharing data from multiple sources into a single virtual data store>Aggregating physical resources spread over multiple physical devices into a single virtual device>Abstracting the complexity of physical infrastructure behind a simple web interface>Logically partitioning physical compute and storage devices into multiple smaller virtual devices_@Sharing, aggregating remote resources and abstracting complex infrastructure can all be accomplished using virtualization techniques but they are not of themselves virtualization_#0_+0_-0_%0\n" +
                "AWS!Cloud_Which of the following best describes Infrastructure as a Service products?_$Services that give you direct control over underlying compute and storage resources_Services that hide infrastructure complexity behind a simple interface>Services that provide a service to end users through a public network>Platforms that allow developers to run their code over short periods on cloud servers>Services that give you direct control over underlying compute and storage resources_@IaaS or Infrastructure as a Service gives full infrastructure access_#0_+0_-0_%0\n" +
                "AWS!Cloud_Which of the following best describes Platform as a Service products?_$Services that hide infrastructure complexity behind a simple interface_Platforms that allows developers to run their code over short periods on cloud servers>Services that give you direct control over underlying compute and storage resources>Services that provide a service to end users through a public network>Services that hide infrastructure complexity behind a simple interface_@Platform as a Service or PaaS masks complexity_#0_+0_-0_%0\n" +
                "Cloud!Lambda_Which of the following best describes Software as a Service products?_$Services that provide a service to end users through a public network_Services that give direct control over underlying compute and storage resources>Services that hide infrastructure complexity behind a simple interface>Platforms that allow developers to run their code over short periods on cloud services>Services that provide a service to end users through a public network_@Any one can use SaaS or Software as a Service due to its simplicity in providing end users services_#0_+0_-0_%0\n" +
                "AWS!Cloud_Which of the following best describes scalability?_$The ability of an application to automatically add preconfigured compute resources to meet increasing demand_The ability of an application to increase or decrease compute resources to match changing demand>The ability to more densely pack virtualized resources onto a single physical server>The ability to bill resource usage using pay per user model>The ability of an application to automatically add preconfigured compute resources to meet increasing demand_@Increasing or decreasing compute resources better describes elasticity. Efficient use of virtualized resources and billing models are not related directly to scalability_#0_+0_-0_%0\n" +
                "AWS!Cloud_Which one of the following characteristics help AWS provide such scalable services?_$Its highly automated infrastructure administration systems_The value of its capitalized assets>Its geographic reach>Its highly automated infrastructure administration systems_@Capitalized assets and geographic reach are important but do not have a direct impact on operational scalability_#0_+0_-0_%0\n" +
                "AWS!Cloud_Which of the following best describes elasticity?_$The ability of an application to increase or decrease compute resources to match changing demand_The ability to more densely pack virtualized resources onto single physical server>The ability to bill resource usage using a pay per user model>the ability of an application to automatically add preconfigured compute resources to meet increasing demand>The ability of an application to increase or decrease compute resources to match changing demand_@Pre configuring compute instances before they are used to scale up an application is an element of scalability rather than elasticity. Efficient use of virtualized resources and billing models are not related directly to elasticity._#0_+0_-0_%0\n" +
                "AWS!Cloud_Which of the following characteristics help AWS provide such scalable services?_$The enormous number of servers it operates_The value of its capitalized assets>Its geographic reach>The enormous number of servers it operates_@Capitalized assets and geographic reach are important but do not have a direct impact on operational scalability_#0_+0_-0_%0\n" +
                "Account!EC2!EBS!S3!Billing_You want to experiment with deploying a web server on an EC2 instance. Which one of the following resources can you include to make that work while remaining within the Free Tier?_$A t2.micro instance type EC2 instance_A 5 GB bucket on S3>Two 20 GB solid state Elastic Block Store or EBS drive>A t2.micro instance type EC2 instance_@S3 buckets which while available in such volumes  under the Free Tier are not necessary for an EC2 instance. Since the maximum total EBS space allowed by the Free Tier is 30 GB, two 20 GB would not be covered_#0_+0_-0_%0\n" +
                "Account!EC2!Billing_Which of the following EC2 services can be used without charge under the Free Tier?_$t2.micro EC2 instance type instances for a total of 750 hours per month_Any single EC2 instance type as long as it urns for less than one hour per day>Any single EC2 instance type as long as it urns for less than 75 hours per month>A single t2.micro EC2 instance type instance for 750 hours per month>t2.micro EC2 instance type instances for a total of 750 hours per month_@Only the t2.micro instance type is Free Tier eligible and any combination of t2.micro instances can run up to a total of 750 hours per month_#0_+0_-0_%0\n" +
                "Billing!Account_Which one of the following tools are available to ensure you do not accidentally run past your Free Tier limit and incur unwanted cost?_$Automated email alerts when activity approaches the Free Tier limits_Billing and Cost Management section on the Top Free Tier Services Dashboard>The Billing Preferences Dashboard>Automated email alerts when activity approaches the Free Tier limits_@There is no Top Free Tier Services Dashboard or a Billing Preferences Dashboard. _#0_+0_-0_%0\n" +
                "Account!EC2!EBS!S3!Billing_You want to experiment with deploying a web server on an EC2 instance. Which of the following resources can you include to make that work while remaining within the Free Tier?_$A 30 GB solid state Elastic Block Store or EBS drive_A 5 GB bucket on S3>Two 20 GB solid state Elastic Block Store or EBS drive>A 30 GB solid state Elastic Block Store or EBS drive_@S3 buckets which while available in such volumes  under the Free Tier are not necessary for an EC2 instance. Since the maximum total EBS space allowed by the Free Tier is 30 GB, two 20 GB would not be covered_#0_+0_-0_%0\n" +
                "Account!Billing!Glacier!CloudWatch_Which one of the following usage will always be cost free even after your accounts Free Tier has expired?_$10 GB of data retrievals from Amazon Glacier per month_One million API calls per month on Amazon API Gateaway>500 MB per month of free storage on the Amazon Elastic Container Registry or ECR>10 GB of data retrievals from Amazon Glacier per month_@The API calls per month and ECR free storage are available only under the Free Tier_#0_+0_-0_%0\n" +
                "Billing!Account_Which of the following tools are available to ensure you do not accidentally run past your Free Tier limit and incur unwanted cost?_$The Top Free Services by Usage section on the Billing and Cost Management Dashboard_Billing and Cost Management section on the Top Free Tier Services Dashboard>The Billing Preferences Dashboard>The Top Free Services by Usage section on the Billing and Cost Management Dashboard_@There is no Top Free Tier Services Dashboard or a Billing Preferences Dashboard. There is however a Top Free Tier Services section not Dashboard_#0_+0_-0_%0\n" +
                "Account!Billing!Glacier!CloudWatch_Which of the following usage will always be cost free even after your accounts Free Tier has expired?_$10 custom monitoring metrics and 10 alarms on Amazon CloudWatch_One million API calls per month on Amazon API Gateaway>500 MB per month of free storage on the Amazon Elastic Container Registry or ECR>10 custom monitoring metrics and 10 alarms on Amazon CloudWatch_@The API calls per month and ECR free storage are available only under the Free Tier_#0_+0_-0_%0\n" +
                "Account!Billing!CLI_Which of the following is likely to be an accurate source of AWS pricing information?_$AWS online documentation relating to a particular service_Wikipedia pages relating to a particular service>The AWS Command line Interface or CLI>The AWS Total Cost of Ownership Calculator>AWS online documentation relating to a particular service_@Wikipedia pages are not updated or detailed enough to be helpful in this respect. The AWS CLI is not likely to have much pricing information. The TCO Calculator should not be used for specific and up to date information about service pricing_#0_+0_-0_%0\n" +
                "AWS!Account_Which of the following will probably not affect the pricing for an AWS service?_$Requests for raising the available service limit_AWS Region>The volume of data saved to an S3 bucket>The volume of data egress from an Amazon Glacier vault>Requests for raising the available service limit_@Pricing will normally change based on the volume of service units you consume and often between AWS Regions_#0_+0_-0_%0\n" +
                "AWS!Account!Billing_Which one of the following Simple Monthly Calculator selections will likely have an impact on other configuration choices on the page?_$Free Usage Tier_Calculate By Month or Year>Include Multiple Organizations>Free Usage Tier_@Calculate By Month or Year is actually not an option in the Simple Monthly Calculator. Also Include Multiple Organizations would not be useful since the calculator calculates only cost by usage_#0_+0_-0_%0\n" +
                "Account!Billing_Which of the following is a limitation of the AWS Simple Monthly Calculator?_$Not all AWS services are included_You can calculate resource use for only one service at a time>The pricing is seldom updated and does not accurately reflect current pricing>You are not able to specify specific configuration parameters>Not all AWS services are included_@You can calculate cost for all multi service stack. The calculator pricing is kept up to date. You can specify very detailed configurations parameters_#0_+0_-0_%0\n" +
                "AWS!Account!Billing_Which of these Simple Monthly Calculator selections will likely have an impact on other configuration choices on the page?_$Choose Region_Calculate By Month or Year>Include Multiple Organizations>Choose Region_@Calculate By Month or Year is actually not an option in the Simple Monthly Calculator. Also Include Multiple Organizations would not be useful since the calculator calculates only cost by usage_#0_+0_-0_%0\n" +
                "Account!Billing_Which of the following is not an included parameter in the AWS Total Cost of Ownership Calculator?_$The tax implications of a cloud deployment_Labor cost of an on premises deployment>Networking costs of an on premises deployment>Electricity cost of an on premises deployment>The tax implications of a cloud deployment_@The calculator covers all significant costs associated with an on premises deployment but does not include tax implications_#0_+0_-0_%0\n" +
                "Account!Billing_Which of the following AWS Total Cost of Ownership Calculator parameters is likely to have the greatest impact on cost?_$Number of servers_Currency>AWS Region>Guest OS>Number of servers_@The currency you choose to use will have little impact on the price. The guest OS and region will make only a minor difference._#0_+0_-0_%0\n" +
                "Storage!Cloud!AWS_Which of the following best describes one possible reason for AWS service limits?_$To prevent individual customers from accidentally launching a crippling level of resource consumption_To more equally distribute available resources between customers from different regions>To allow customers to more gradually increase their deployments>Because there are logical limits to the ability of AWS resources to scale upward>To prevent individual customers from accidentally launching a crippling level of resource consumption_@Resource Limits exist only within individual regions due to the fact that limits on one region does not impact another. There is no logistical reason that customers cannot scale up deployments at any rate. There are no logical limits to the ability of AWS resources to scale upward._#0_+0_-0_%0\n" +
                "Storage!Cloud!AWS_Is it always possible to request service limits increases from AWS?_$No, some services limits are absolute cannot change_Yes, all services limits can be increased>No, a limit can never be increased>Service limits are defaults. They can be increased or decreased on demand>No, some services limits are absolute cannot change_@While most services limits are soft and can be raised on request, there are some services limits that are absolute or final_#0_+0_-0_%0\n" +
                "Account!Billing_Which is the best place to get a quick summary of a months spend for your account?_$Billing and Cost Management Dashboard_Budgets>Cost Explorer>Cost and usage reports>Billing and Cost Management Dashboard_@The Cost Explorer page provides in depth or customizable details and so does the Cost and Usage Reports. Budgets just allows you to set alerts based on usage._#0_+0_-0_%0\n" +
                "Account!Billing_What is the main goal for creating a Usage budget type in AWS Budgets?_$To track particular categories of resource consumption_To correlate usage per unit cost to understand an accounts cost efficiency>To track the status of any active reserved instance on your account>To monitor cost being incurred against your account>To track particular categories of resource consumption_@Reservation budgets track the status of any active reserved instances on an account. Cost budgets monitor costs being incurred against your account. There is no budget type that correlates usage per unit cost to understand an account cost efficiency_#0_+0_-0_%0\n" +
                "Account!Billing_Which of the following is not a setting you can configure in a Cost budget?_$Owner_Period such as monthly, quarterly>Instance type>Start and stop dates>Owner_@You can configure all the others except which owner owns which resource._#0_+0_-0_%0\n" +
                "AWS!Scenario_Which one of the following scenarios would be a good use case for AWS Organizations?_$A single company with multiple AWS accounts that wants a single place to administrate everything_An organization that provides AWS access to large teams of its developers and admins >A company with two distinct operational units each with its own accounting system and AWS account>A single company with multiple AWS accounts that wants a single place to administrate everything_@Companies with multiple users of resources in a single AWS account would not benefit from AWS Organizations. The same goes for a company with separated units. The value of AWS Organizations is in integrating administration of related accounts_#0_+0_-0_%0\n" +
                "Account!Billing_What is the purpose of cost allocation tags?_$To help identify resources for the purpose of tracking account spending_To associate spend limits to automatically trigger resource shutdowns when necessary>To help identify the purpose and owner of a particular running resource to better understand and control deployments>To visually associate events with billing periods>To help identify resources for the purpose of tracking account spending_@Tags are passive so they cannot trigger anything automatically. Resource tags are the ones meant to help you understand and control deployments not allocation tags. Also tags are not associated with particular billing periods_#0_+0_-0_%0\n" +
                "AWS!Scenario_Which of the following scenarios would be a good use case for AWS Organizations?_$A company that has integrated some operations with an upstream vendor _An organization that provides AWS access to large teams of its developers and admins>A company with two distinct operational units each with its own accounting system and AWS account>A company that has integrated some operations with an upstream vendor _@Companies with multiple users of resources in a single AWS account would not benefit from AWS Organizations. The same goes for a company with separated units. The value of AWS Organizations is in integrating administration of related accounts_#0_+0_-0_%0\n" +
                "Account!Billing_Which of these tools lets you design graphs within the browser interface to track your account spending?_$Cost Explorer_Budgets>Reports>Consolidating Billing>Cost Explorer_@Budgets are used to set alerts. Reports provide CSV formatted data for offline processing. Consolidated billing is for administrating resources across multiple AWS accounts_#0_+0_-0_%0\n" +
                "Scenario!AWS_Your company is planning a major deployment on AWS. While the design and testing stages are still in progress, which of the following plans will provide the best blend of support and cost savings?_$Business plan_Basic plan>Developer plan>Enterprise>Business plan_@The Basic plan will not provide any personalized support. The Developer plan is cheaper but there is limited access to support professionals. The Business plan does offer 24 hours and seven days email, chat and phone access to an engineer. So until deployment the Business plan makes the most sense. Enterprise plan will not be very cost effective because because it is 15,000 dollars a month minimum while the Business plan is much lower_#0_+0_-0_%0\n" +
                "Scenario!AWS_Your Web development team is actively gearing up for a deployment of an e commerce site. During these early stages of the process, individual developers are running into frustrating conflicts and configuration problems. Which of the following plans will provide the best blend of support and cost savings?_$Developer Plan_Basic Plan>Business Plan>Enterprise>Developer Plan_@Using the public documentation available  through the Basic plan will not be enough to address your specific needs. The Business and Enterprise plans are not necessary as you do not yet have production deployment_#0_+0_-0_%0\n" +
                "Scenario!AWS_Your corporate website was offline last week for more than two hours which caused serious consequences, including the early retirement of your CTO. Your engineers have been having a lot of trouble tracking down the source of the outage and admit that they need outside help. Which of the following will most likely meet the need?_$Enterprise Plan_Basic Plan>Developer Plan>Business Plan>Enterprise Plan_@The lower three support tiers which are Basic, Developer and Business,  provided limited access to only lower level support professionals, while the Enterprise plan provides full access to senior engineers and dedicates a technical account manager or TAM as your resource for all your AWS needs_#0_+0_-0_%0\n" +
                "AWS!Account_For which of the following will AWS provide direct 24 hour support to all users even those on the Basic Support Plan?_$Help with making a bill payment to AWS_Help with infrastructure under a massive denial of service attack>Help with failed and unavailable infrastructure>help with accessing your infrastructure via the AWS CLI>Help with making a bill payment to AWS_@Basic plan customers are given customer support access only for account management issues and not for technical support or security breaches_#0_+0_-0_%0\n" +
                "AWS!Account_The primary purpose of an AWS technical account manager is to?_$Provide deployment guidance and advocacy for Enterprise Support customers_Provide 24 hour and seven days a week customer service for your AWS account>Provide deployment guidance and advocacy for Business Support customers>Provide strategic cost estimates for Enterprise Support customers>Provide deployment guidance and advocacy for Enterprise Support customers_@The Technical Account Manager or TAM is only available for Enterprise Support Customers. The primary function is one of guidance and advocacy_#0_+0_-0_%0\n" +
                "Scenario!AWS!Account_Your Linux based EC2 instance requires a patch to  a Linux kernel module. The problem is that patching the module will, for some reason break connection between you instance and data in an S3 bucket. Your team does not know if it is possible to work around this problem. Which is the most cost effective AWS plan through which support professionals will try to help?_$Business Plan_Developer Plan>Enterprise Plan>NO plan covers this kind of support>Business Plan_@Only the Business and Enterprise plans include help with troubleshooting interoperability between AWS resources  and third party software and operating systems. The Business Plan is the least expensive that will get you this level of support_#0_+0_-0_%0\n" +
                "AWS!Security_Which of the following is an AWS Region for which customer access is restricted?_$AWS GovCloud_AWS Admin>US DOD>Asia Pacific >AWS GovCloud_@AWS Admin and US DOD are actually made up. Asia Pacific  region is a normal region anyone can access. It is the AWS GovCloud which has restricted access because it is obviously for the government._#0_+0_-0_%0\n" +
                "RDS!Route53!EC2!CloudFront_Which one of the following is globally based AWS services?_$Route 53_RDS>EC2>Route 53_@Relational Database Service RDS and EC2 both use resources that can exist in only a Region._#0_+0_-0_%0\n" +
                "AWS!EC2!CLI_When you request a new virtual machine instance in E2 you instance will automatically launch into currently selected value of which the following?_$Region_Service>Subnet>Availability Zone>Region_@EC2 instances automatically launch into the Region you have currently selected. You can manually select the subnet that is associated with a particular Availability Zone for you new EC2 instance but there is no default choice._#0_+0_-0_%0\n" +
                "RDS!Route53!EC2!CloudFront_Which of the following is globally based AWS services?_$CloudFront_RDS>EC2>CloudFront_@Relational Database Service RDS and EC2 both use resources that can exist in only a Region._#0_+0_-0_%0\n" +
                "AWS!Account!IAM_What is the primary function of the AWS IAM service?_$Identity and Access Management_Access key management>SSH key pair management>Federated access management>Identity and Access Management_@IAM or Identity Access Management is primarily focused on helping you control access to your AWS resources. KMS handles access keys. EC2 manages SSH key pairs. Although IAM does touch on federated management it is not its key purpose_#0_+0_-0_%0\n" +
                "IAM!Account!AWS_What is an IAM role?_$Permissions granted a trusted entity over specified AWS resources_A set of permissions allowing access to specified AWS resources>A set of IAM users given  permission to access specified AWS resources>Permissions granted an IAM user over specified AWS resources>Permissions granted a trusted entity over specified AWS resources_@An IAM role is meant to be assigned to a trusted entity such as another AWS service or a federated identity. A set of permissions could refer to a policy. A set of IAM users could be referred to as a group_#0_+0_-0_%0\n" +
                "Console!Security!Account_Which of the following credentials can you use to log into the AWS Management Console?_$IAM or Identity and Access Management username_Access key ID>Account alias>Account ID>IAM or Identity and Access Management username_@You can sign in as the root user or as an IAM user. Although you need to specify the account alias or account ID to log in as an IAM user, those are credentials. You cannot log in to the console using an access key ID_#0_+0_-0_%0\n" +
                "Console!Security_How long will your session with the AWS Management Console remain active?_$12 hours_6 hours>8 hours>15 minutes>12 hours_@12 hours is the limit, once exceeded you will have to log in again._#0_+0_-0_%0\n" +
                "EC2!AMI_What is the function of an EC2 AMI?_$To serve as a source from which an instance primary storage volume is built_To define the hardware profile used by an EC2 instance>To serve as an instance storage volume for high volume data processing operations>To define the way data streams are managed by EC2 instances>To serve as a source from which an instance primary storage volume is built_@An instances hardware profile is defined by the instance type. High Volume or low volume data processing operations and data streams can be handled using any storage volume or on any instance_#0_+0_-0_%0\n" +
                "S3!AWS!EBS!Storage_What is one of the major differences between S3 or Simple Storage Service and Elastic Block Store or EBS?_$EBS stores volumes_EBS stores snapshots>S3 stores volumes>EBS stores objects>EBS stores volumes_@S3 is an object storage service while EBS is a block storage service that stores volumes. EBS snapshots are stored in S3. S3 does not store volumes and EBS does not store objects._#0_+0_-0_%0\n" +
                "S3!AWS!Console!Storage_When trying to create an S3 bucket named documents, AWS informs you that the bucket name is already in use. What should you do in order to create a bucket?_$Use a globally unique bucket name_Use a different region>Use a different storage class>Use a longer name>Use a shorter name>Use a globally unique bucket name_@Bucket names must be globally unique across AWS, irrespective of Region. Also the length  of the bucket name has to be between 3 and 63 characters long. Storage classes are configured on a per object basis and have no impact on bucket naming._#0_+0_-0_%0\n" +
                "S3!AWS!EBS!Storage_What is a major differences between S3 or Simple Storage Service and Elastic Block Store or EBS?_$S3 stores objects_EBS stores snapshots>S3 stores volumes>EBS stores objects>S3 stores objects_@S3 is an object storage service while EBS is a block storage service that stores volumes. EBS snapshots are stored in S3. S3 does not store volumes and EBS does not store objects._#0_+0_-0_%0\n" +
                "Database!Storage_Which type of database stores data in columns and rows?_$Relational_Non relational>Key value store>Document>Relational_@A relational database stores data in columns called attributes and rows called records. Non relational databases, which include key value stores and document stores, stores data in collection or items but do not use columns or rows_#0_+0_-0_%0\n" +
                "VPC!Cloud_Which one of the following are true of a default VPC?_$AWS creates a default VPC in each region_A default VPC spans multiple regions.>AWS creates a default VPC in each Availability Zone>AWS creates a default VPC in each region_@For each account, AWS creates a default VPC in each Region. A VPC spans all Availability Zones within a Region. VPCs do not span Regions._#0_+0_-0_%0\n" +
                "Database!Storage_Which of the following Structured Query Language or SQL statements, can you use to write data to a relational database table?_$INSERT_CREATE>QUERY>WRITE>INSERT_@The SQL INSERT statement can be used to add data to a relational database. CREATE can be used to create a table but not add data to it. WRITE is not a valid SQL command._#0_+0_-0_%0\n" +
                "VPC!Cloud_Which one of the following are true regarding subnets?_$A subnet must have a CIDR that is a subnet of the CIDR of the VPC in which it resides_A VPC must have at least two subnets>A subnet spans multiple Availability Zones>A subnet must have a CIDR that is a subnet of the CIDR of the VPC in which it resides_@A subnet exists in only one Availability Zone and it must have a CIDR that is a subnet of CIDR of the VPC in which it resides. There is no requirement for a VPC to have two subnets, but it must have at least one._#0_+0_-0_%0\n" +
                "VPC!Cloud_Which of the following are true of a default VPC?_$By default, each default VPC is available to one AWS account_A default VPC spans multiple regions.>AWS creates a default VPC in each Availability Zone>By default, each default VPC is available to one AWS account_@For each account, AWS creates a default VPC in each Region. A VPC spans all Availability Zones within a Region. VPCs do not span Regions._#0_+0_-0_%0\n" +
                "VPC!Cloud_Which of the following are true regarding subnets?_$A subnet spans one Availability Zone_A VPC must have at least two subnets>A subnet spans multiple Availability Zones>A subnet spans one Availability Zone_@A subnet exists in only one Availability Zone and it must have a CIDR that is a subnet of CIDR of the VPC in which it resides. There is no requirement for a VPC to have two subnets, but it must have at least one._#0_+0_-0_%0\n" +
                "AWS!CloudFormation!Console_What is another format which CloudFormation templates support?_$YAML_XML>HTML>YAML_@CloudFormation templates are written in YAML or JSON format_#0_+0_-0_%0\n" +
                "AWS!CloudFormation_Which of the following is an advantage of using CloudFormation?_$It lets you create multiple separate AWS environments using a single template_It uses the popular Python programming language>It prevents unauthorized manual changes to resources>It can create resources outside of AWS>It lets you create multiple separate AWS environments using a single template_@CloudFormation can create AWS resources and manages them collectively in a stack. Templates are written in the CloudFormation language, not Python. CloudFormation cannot create resources outside of AWS. CloudFormation does not prevent manual changes to resources in a stack_#0_+0_-0_%0\n" +
                "AWS!CloudFormation!Console_What is one format which CloudFormation templates support?_$JSON_XML>HTML>JSON_@CloudFormation templates are written in YAML or JSON format_#0_+0_-0_%0\n" +
                "AWS!Cloud_Which of the following is an example of applying the principles of the security pillar of the Well Architected Framework?_$Granting each AWS user their own IAM username and password_Creating a security group rule to deny access to unused ports>Deleting an empty S3 bucket>Granting each AWS user their own IAM username and password_@Security is about protecting the confidentiality, integrity and availability of data.Granting each AWS user their own IAM username and password makes it possible to ensure the confidentiality of data.Deleting an empty S3 bucket does not help with any of these. It is not possible to create a security group rule that denies access to unused ports since security groups deny any traffic that is not explicitly allowed._#0_+0_-0_%0\n" +
                "AWS!Cloud_Which of the following is not one of the pillars of the Well Architecture Framework?_$Resiliency_Performance Efficiency>Reliability>Security>Cost optimization>Resiliency_@The five pillars of the Well Architecture Framework.Reliability.Efficiency.Security.Cost Optimization.Operational Excellence.Resiliency is not one of them_#0_+0_-0_%0\n" +
                "Scenario!EC2!Cloud_You are hosting a web application on two EC2 instances in an Auto Scaling group. The performance of the application is consistently acceptable. Which one of the following can help maintain or improve performance efficiency?_$Implementing policies to prevent the accidental termination of EC2 instances in the same Auto Scaling group_Monitoring for unauthorized access>Doubling the number of instances in the Auto Scaling group.>Implementing policies to prevent the accidental termination of EC2 instances in the same Auto Scaling group_@Preventing the accidental termination of an EC2 instance in the Auto Scaling group can avoid overburdening and causing performance issues on the remaining instance, especially during busy times. Doubling the number of instances might improve performance but because performance is already acceptable doing this would be inefficient. Monitoring for unauthorized access alone will not improve performance. _#0_+0_-0_%0\n" +
                "AWS!Cloud_Which one of the following is an example of applying the principles of the security pillar of the Well Architected Framework?_$Enabling S3 versioning_Creating a security group rule to deny access to unused ports>Deleting an empty S3 bucket>Enabling S3 versioning_@Security is about protecting the confidentiality, integrity and availability of data.Enabling S3 versioning protects the integrity by maintaining a backup of an object. Deleting an empty S3 bucket does not help with any of these. It is not possible to create a security group rule that denies access to unused ports since security groups deny any traffic that is not explicitly allowed._#0_+0_-0_%0\n" +
                "S3!Billing!Account_Which one of the following can help achieve cost optimization?_$Deleting unused application load balancers_Deleting empty S3 buckets>Deleting unused VPCs>Deleting unused application load balancers_@Unused application load balancers can reduce cost since you are charged for it. Deleting unused VPCs and emptying S3 buckets will not reduce cost since they do not cost anything_#0_+0_-0_%0\n" +
                "Scenario!EC2!Cloud_You are hosting a web application on two EC2 instances in an Auto Scaling group. The performance of the application is consistently acceptable. Which of the following can help maintain or improve performance efficiency?_$Using CloudFront_Monitoring for unauthorized access>Doubling the number of instances in the Auto Scaling group.>Using CloudFront_@Using CloudFront can help improve performance for end users by caching the content in n edge location close to them. Doubling the number of instances might improve performance but because performance is already acceptable doing this would be inefficient. Monitoring for unauthorized access alone will not improve performance. _#0_+0_-0_%0\n" +
                "S3!Billing!Account_Which of the following can help achieve cost optimization?_$Deleting unused S3 objects_Deleting empty S3 buckets>Deleting unused VPCs>Deleting unused S3 objects_@Deleting unused S3 objects can reduce cost since you are charged for it. Deleting unused VPCs and emptying S3 buckets will not reduce cost since they do not cost anything_#0_+0_-0_%0\n" +
                "CloudFormation!CLI!AWS_Give a reason why you would you use CloudFormation to automatically create resources for a development environment instead of creating them using AWS CLI commands?_$CloudFormation stack updates help unsure that changes to one resource do not break another_Resources created by CloudFormation always work as expected>CloudFormation can provision resources faster than the AWS CLI>CloudFormation stack updates help unsure that changes to one resource do not break another_@Resources CloudFormation creates are organized into stacks. The you update a stack, CloudFormation analyzes the relationships among resources in the stack and updates dependent resources as necessary. This does not mean that any resources you create using CloudFormation will work as you expect. Provisioning resources using CloudFormation is not necessarily faster than using the AWS CLI._#0_+0_-0_%0\n" +
                "AWS!CloudFormation_What is an advantage of using parameters in a CloudFormation template?_$Allow customizing a stack without changing the template_Prevent unauthorized users from using the template>Prevent stack updates>Allow multiple stacks to be created from the same template>Allow customizing a stack without changing the template_@Parameters let you input customizations when creating a CloudFormation stack without having to modify the underlying template. Parameters do not prevent stack updates or unauthorized changes. A template can be used to create multiple stacks regardless of whether it uses parameters._#0_+0_-0_%0\n" +
                "CloudFormation!CLI!AWS_Why would you use CloudFormation to automatically create resources for a development environment instead of creating them using AWS CLI commands?_$Resources CloudFormation creates are organized into stacks and can be managed as a single unit_Resources created by CloudFormation always work as expected>CloudFormation can provision resources faster than the AWS CLI>Resources CloudFormation creates are organized into stacks and can be managed as a single unit_@Resources CloudFormation creates are organized into stacks. The you update a stack, CloudFormation analyzes the relationships among resources in the stack and updates dependent resources as necessary. This does not mean that any resources you create using CloudFormation will work as you expect. Provisioning resources using CloudFormation is not necessarily faster than using the AWS CLI._#0_+0_-0_%0\n" +
                "Security!Cloud!VPC_What is one difference between a security group and a network access control list or NACL?_$A security group operates at the instance level_A network access control list operates at the instance level>A security group operates at the subnet level>A security group operates at the instance level_@A security group is a firewall that operates at the instance level._#0_+0_-0_%0\n" +
                "Security!Cloud!VPC_Which of the following is true of a new security group?_$It contains an outbound rule allowing access to any IP address_It contains an inbound rule denying access from public IP addresses>It contains an outbound rule denying access to public IP addresses>It contains an inbound rule allowing access from any IP address>It contains an inbound rule denying access from any IP address>It contains an outbound rule allowing access to any IP address_@When you create a security group, it contains an outbound rule that allows access to any IP address. It does not contain an inbound rule by default. Security group rules can only permit access not key it so any traffic not explicitly allowed will be denied_#0_+0_-0_%0\n" +
                "Security!Cloud!VPC_What is the difference between a security group and a network access control list or NACL?_$A network access control list operates at the subnet level._A network access control list operates at the instance level>A security group operates at the subnet level>A network access control list operates at the subnet level._@A network access control list is a firewall that operates at the subnet level._#0_+0_-0_%0";
        return questionings;
    }

    static String theQQ () {
        String questioningss =
                "Storage!S3!Glacier!EBS_Which of these tasks can S3 object life cycle configurations perform automatically?_$Deleting old object versions_Deleting old buckets>Moving objects to an EBS volume>Deleting old object versions_@Object Life cycle configurations can perform transition or expiration actions based on an objects age. Transition actions can move objects between storage classes such as Standard and Glacier. Expiration actions can delete object and object versions. Object life cycle configuration cannot delete buckets or move objects to an EBS volume._#0_+0_-0_%0\n" +
                        "Database!DynamoDB_Which of the following statements is true regarding non relational databases?_$You do not have to define all types of data that a table can store before adding data to it_You can create only one table>No primary key is required>You cannot store data with a fixed structure>You do not have to define all types of data that a table can store before adding data to it_@A non relational database is schemaless, which means that there is no need to predefine all the types of data you will sore in a table. This does not preclude you from storing data with a fixed structure, as non relational databases can store virtually any kind of data. A primary key is required to uniquely identify each item in a table. Creating multiple tables is allowed but most applications that use non relational databases use only one table._#0_+0_-0_%0\n" +
                        "Storage!S3!Glacier!EBS_Which one of these tasks can S3 object life cycle configurations perform automatically?_$Moving objects to Glacier_Deleting old buckets>Moving objects to an EBS volume>Moving objects to Glacier_@Object Life cycle configurations can perform transition or expiration actions based on an objects age. Transition actions can move objects between storage classes such as Standard and Glacier. Expiration actions can delete object and object versions. Object life cycle configuration cannot delete buckets or move objects to an EBS volume._#0_+0_-0_%0\n" +
                        "Database!DynamoDB_What is a no SQL database?_$schemaless non relational database_A nonrelational database without primary keys>A schemaless relational database>A relational database with primary keys_@A no SQL database is another term for a none relational database. By definition, non relational databases are schemaless and must use primary keys. There is no such thing as schemaless relational database. No SQL is never used to describe a relational database of any kind._#0_+0_-0_%0\n" +
                        "S3!Storage!Security_What is an example of a method which can be used to grant anonymous access to an object in S3?_$Bucket policies_User policies>Security groups>Bucket policies_@You can use bucket policies or access control list, also known as ACL, to grant anonymous users access to an object in S3. You cannot use user policies to do this although you can use them to grant IAM principals access to objects. Security groups control access to resources in a virtual private cloud or VPC and are not used to control access to objects in S3_#0_+0_-0_%0\n" +
                        "Storage!S3!Glacier!EBS_Which tasks can S3 object life cycle configurations perform automatically?_$Deleting old objects_Deleting old buckets>Moving objects to an EBS volume>Deleting old objects_@Object Life cycle configurations can perform transition or expiration actions based on an objects age. Transition actions can move objects between storage classes such as Standard and Glacier. Expiration actions can delete object and object versions. Object life cycle configuration cannot delete buckets or move objects to an EBS volume._#0_+0_-0_%0\n" +
                        "S3!Storage!Security_Which method can be used to grant anonymous access to an object in S3?_$Access control lists_User policies>Security groups>Access control lists_@You can use bucket policies or access control list, also known as ACL, to grant anonymous users access to an object in S3. You cannot use user policies to do this although you can use them to grant IAM principals access to objects. Security groups control access to resources in a virtual private cloud or VPC and are not used to control access to objects in S3_#0_+0_-0_%0\n" +
                        "EC2!AMI_Which one of the following could be included in an EC2 AMI?_$A software application stack_A networking configuration>An instance type definition>A software application stack_@AMIs can be created that provide both base operating system and pre installed application. They would not however include any networking or hardware profile information. Networking or hardware profile information are largely determined by the instance type_#0_+0_-0_%0\n" +
                        "Console!AWS!AMI_Where can you find a wide range of verified AMIs from both AWS and third party vendors?_$AWS Marketplace_Quick Start>Community AMIs>My AMIs>AWS Marketplace_@The Quick Start includes only the few dozen most popular AMIs. The Community tab includes thousands of publicly available AMIs whether they are verified or not. The My AMIs tab only includes AMIs created from your account_#0_+0_-0_%0\n" +
                        "AWS!EC2_Which one of the following is EC2 instance type families?_$Accelerated computing_c5d.18xlarge>t2.micro>Accelerated computing_@c5d.18xlarge and t2.micro are the names of EC2 instance types not instance families_#0_+0_-0_%0\n" +
                        "EC2!AMI_Which of the following could be included in an EC2 AMI?_$An operating system_A networking configuration>An instance type definition>An operating system_@AMIs can be created that provide both base operating system and pre installed application. They would not however include any networking or hardware profile information. Networking or hardware profile information are largely determined by the instance type_#0_+0_-0_%0\n" +
                        "AWS!EC2_Which of the following is EC2 instance type families?_$Compute optimized_c5d.18xlarge>t2.micro>Compute optimized_@c5d.18xlarge and t2.micro are the names of EC2 instance types not instance families_#0_+0_-0_%0\n" +
                        "IAM!Security_Which one of the following are requirements can you include in an IAM password policy?_$Require at least one uppercase letter_Require at least one space or null character>A number is not mandatory>Require at least one uppercase letter_@Including a space or null character is not a password policy option_#0_+0_-0_%0\n" +
                        "EC2!AWS!Console!Scenario_While looking at the EC2 service console in the AWS Management Console while logged in as the root user, you notice all of your instances are missing. What could be the reason?_$You have selected the wrong region in the navigation bar_You do not have view access>You have selected the wrong Availability Zone in the navigation bar>You do not have an access key>You have selected the wrong region in the navigation bar_@If a resource that should be visible appears to be missing, you may have the wrong Region selected. Since you logged in as the root user you have view access to all resources. You do not need an access key to use the console. You cannot select an Availability Zone in the navigation bar_#0_+0_-0_%0\n" +
                        "IAM!Security_Which of the following options is required in an IAM password policy?_$Require at least one number_Require at least one space or null character>A number is not mandatory>Require at least one number_@Including a space or null character is not a password policy option_#0_+0_-0_%0\n" +
                        "Console!Account_Which of the following is true regarding a resource tag?_$It must have a key_It must be unique within an account>It is case sensitive>It must have a value>It must have a key_@Each resource tag you create must have a key but a value is optional. Tags do not have to be unique within an account and they are case sensitive_#0_+0_-0_%0\n" +
                        "AWS!Account!Security_Which one of the following should you do to secure your AWS root user?_$Enable MFA_Assign the root user to the admins IAM group>Use the root user for day to day administration task.>Enable MFA_@The root user should not be used for day to day admin tasks even as part of an admin group. The goal is to protect root as much as possible_#0_+0_-0_%0\n" +
                        "IAM!Security_Which of the following are requirements you can include in an IAM password policy?_$Require at least one non alphanumeric character_Require at least one space or null character>A number is not mandatory>Require at least one non alphanumeric character_@Including a space or null character is not a password policy option_#0_+0_-0_%0\n" +
                        "AWS!Account_What is a significant architectural benefit of the way AWS designed its regions?_$It can make applications available to end users with lower latency_It can make infrastructure more fault tolerant>It can bring down the price of running servers>It can make applications available to end users with lower latency_@For most uses, distributing your application infrastructure between multiple AZs within a single Region gives them sufficient fault tolerance. While AWS services do enjoy a significant economy of scale, little of that is due to the structure of their Regions. Lower Latency and compliance are the biggest benefits from this list._#0_+0_-0_%0\n" +
                        "AWS!Account!Security_Which of the following should you do to secure your AWS root user?_$Create a strong password_Assign the root user to the admins IAM group>Use the root user for day to day administration task.>Create a strong password_@The root user should not be used for day to day admin tasks even as part of an admin group. The goal is to protect root as much as possible_#0_+0_-0_%0\n" +
                        "AWS!Account_Which one of these is an architectural benefit of the way AWS designed its regions?_$It can make applications more compliant with local regulations_It can make infrastructure more fault tolerant>It can bringdown the price of running>It can make applications more compliant with local regulations_@For most uses, distributing your application infrastructure between multiple AZs within a single Region gives them sufficient fault tolerance. While AWS services do enjoy a significant economy of scale, little of that is due to the structure of their Regions. Lower Latency and compliance are the biggest benefits from this list._#0_+0_-0_%0\n" +
                        "Console!AWS_AWS Documentation is available in a number of formats including which one of the following options?_$Kindle_Microsoft Word or Doc>DocBook>All of these options>None of these options>Kindle_@Although DOC and DocBook are both popular and useful formats, nether is used by AWS for Documentation._#0_+0_-0_%0\n" +
                        "Console!AWS_Which of the following AWS support services does not offer free documentation of some sort?_$AWS Partner Network_AWS Professional Services>The Basic Support plan>The Knowledge Center>None of these options>AWS Partner Network_@The AWS Professional Services site includes tech talk webinars, white papers and blog post. The Basic Support plan includes AWS documentation resources. The Knowledge Center consist of FAQ Documentation._#0_+0_-0_%0\n" +
                        "AWS!Security!Account_Which one of the following Trusted Advisor alerts is available only for accounts on the Business or Enterprise Support Plan?_$Load Balancer Optimization_MFA or Root Account>Service Limits>Load Balancer Optimization_@Both the MFA and Service Limits checks are available for all accounts_#0_+0_-0_%0\n" +
                        "Console!AWS_AWS Documentation is available in a number of formats including which of the following?_$HTML_Microsoft Word or Doc>DocBook>All of these options>None of these options>HTML_@Although DOC and DocBook are both popular and useful formats, nether is used by AWS for Documentation._#0_+0_-0_%0\n" +
                        "AWS!Security!Account_Which of the following Trusted Advisor alerts is available only for accounts on the Business or Enterprise Support Plan?_$IAM Access Key Rotation_MFA or Root Account>Service Limits>IAM Access Key Rotation_@Both the MFA and Service Limits checks are available for all accounts_#0_+0_-0_%0\n" +
                        "AWS!Security!Account_Within the context of Trusted Advisor, what is a false positive?_$An alert for a service state that was actually intentional_A green OK icon for a service state that is failed or failing>A single status icon indicating that your account is completely compliant>Textual indication of a failed state>An alert for a service state that was actually intentional_@An OK status for a failed state is a false negative. There is no single status icon indicating that your account is completely compliant with Trusted Advisor_#0_+0_-0_%0\n" +
                        "AWS!Account!Security_Instances that are running idle should be identified by which of the Trusted Advisor categories?_$Cost Optimization_Performance>Service Limits>Replication>Cost Optimization_@Performance identifies configuration settings that might be blocking performance improvements. Service Limits identifies resource usage that is approaching AWS Region or service limits. There is no Replication Category_#0_+0_-0_%0\n" +
                        "AWS!Security!Account_Data volumes that are not properly backed up is an example of which of these Trusted Advisor categories?_$Fault Tolerance_Performance>Security>Cost Optimization>Fault Tolerance_@Performance identifies configurations settings that might be blocking performance  improvements. Security identifies any failures to use security best practice configurations. Cost optimization identifies any resources that are running and unnecessarily costing money._#0_+0_-0_%0\n" +
                        "AWS!Account_Where will you find information on the limits AWS imposes on the ways you can use your account resources?_$AWS Acceptable Use Policy_AWS User Agreement Policy>AWS Acceptable Use Monitor>AWS Acceptable Use Dashboard>AWS Acceptable Use Policy_@The correct document for this information is the AWS Acceptable Use Policy._#0_+0_-0_%0\n" +
                        "AWS!Console_Which of the following is one of the first places you should look when troubleshooting a failing application?_$Service Health Dashboard_AWS Acceptable Use Monitor>Service Status Dashboard>AWS Billing Dashboard>Service Health Dashboard_@The AWS Billing Dashboard is focused on your account billing issues. Neither the AWS Acceptable Use Monitor nor the Service Status Dashboard actually exists. But nice try._#0_+0_-0_%0\n" +
                        "AWS!Console_According to the AWS Shared Responsibility Model, which one of the following are responsibilities of AWS?_$The security fo the cloud_Security of what is in the cloud>Patching OSs running on the EC2 instances>The security fo the cloud_@What is in the cloud is your responsibility and that includes the administration of EC2 based operating systems_#0_+0_-0_%0\n" +
                        "AWS!Console_According to the AWS Shared Responsibility Model, what is the best way to define the status of the software driving an AWS managed service?_$Whatever the customer can control is the customers responsibility_Everything associated with an AWS managed service is the responsibility of AWS>Whatever is added by the customer is the customers responsibility>Everything associated with an AWS managed service is the responsibility of the customer>Whatever the customer can control is the customers responsibility_@There is no one easy answer as some managed service are pretty much entirely within Amazons sphere and others leave lots of responsibility with the customer. However the key slogan to remember here is, if you can edit it, you own it._#0_+0_-0_%0\n" +
                        "Security!AWS_Which role can the documents provided by AWS Artifact play in your application planning?_$They can help you confirm that your deployment infrastructure is compliant with regulatory standards_They can provide insight into networking and storage design patterns your AWS applications use>They represent AWS infrastructure>They can help you confirm that your deployment infrastructure is compliant with regulatory standards_@AWS Artifact documents are about AWS infrastructure compliant with external standards. They tangentially can also provide insight into best practices. They do not represent internal design or policies_#0_+0_-0_%0\n" +
                        "AWS!Console_According to the AWS Shared Responsibility Model, which of the following are responsibilities of AWS?_$Patching underlying virtualization software running in AWS data centers_Security of what is in the cloud>Patching OSs running on the EC2 instances>Patching underlying virtualization software running in AWS data centers_@Whatever is in the cloud is your responsibility and that includes the administration of EC2 based operating systems_#0_+0_-0_%0\n" +
                        "Security!AWS_What role can the documents provided by AWS Artifact play in your application planning?_$They can provide insight into various regulatory and industry standards that represent best practices_They can provide insight into networking and storage design patterns your AWS applications use>They represent AWS infrastructure>They can provide insight into various regulatory and industry standards that represent best practices_@AWS Artifact documents are about AWS infrastructure compliant with external standards. They tangentially can also provide insight into best practices. They do not represent internal design or policies_#0_+0_-0_%0\n" +
                        "Security!AWS_What is the purpose of the Service Organization Controls reports found on AWS Artifact?_$They attest to AWS infrastructure compliance with data accountability standards like Sarbanes Oxley_They can be used to help design secure and reliable credit card transaction applications>They guarantee that all AWS based applications are by default compliant with Sarbanes Oxley standards>They are an official ongoing risk assessment profiler for AWS based deployment>They attest to AWS infrastructure compliance with data accountability standards like Sarbanes Oxley_@SOC is not primarily about guidance or risk assessment and it is definitely not a guarantee of the state of your own deployments. SOC reports are reports of audits on AWS infrastructure that you can use as part of your own reporting requirements_#0_+0_-0_%0\n" +
                        "AWS!Security_What does Key Management Service or KMS use to encrypt objects stored on your AWS account?_$Customer master key_SSH master key>KMS master key>Client side master key>Customer master key_@A Client side master key is used to encrypt objects objects before they reach AWS such as S3 specifically. There are no keys commonly known as either SSH or KMS master keys_#0_+0_-0_%0\n" +
                        "RDS!EBS!S3!DynamoDB!Security_Which of the following AWS resources cannot be encrypted using Key Management Service or KMS?_$Existing AWS Elastic Block Store volumes_RDS databases>S3 Buckets>DynamoDB Databases>Existing AWS Elastic Block Store volumes_@You can only encrypt an EBS volume at creation not after creation._#0_+0_-0_%0\n" +
                        "Billing!Account_Which Cost Explorer reports shows the amount of money you have saved using reserved instances?_$Reservation Utilization_Daily cost>Reservation Coverage>Monthly EC2 running hours cost and usage>Reservation Utilization_@The reservation utilization report shows how much you have saved using reserved instances. The reservation coverage report shows how much you could have potentially saved have you purchased reserved instances. The daily cost and monthly running hours costs and usage reports do not know how much you have saved using reserved instances._#0_+0_-0_%0\n" +
                        "Account!S3!Lambda!RDS!Billing_Which of the following services allow you to purchase reserved instances to save money?_$Amazon Relational Database Service or RDS_Lambda>S3>AWS Fargate>Amazon Relational Database Service or RDS_@RDS lets you purchase reserved instances to save money. Lambda, S3, and Fargate do not use instances_#0_+0_-0_%0\n" +
                        "Scenario!S3!CloudWatch!CloudTrail_You want to log every object downloaded from an S3 bucket in a specific region. You want to retain these logs indefinitely and search them easily. What is one of the most cost effective way to do this?_$Stream CloudTrail logs to CloudWatch logs_Use CloudTrail event history>Enable CloudTrail logging of global service events>Stream CloudTrail logs to CloudWatch logs_@Creating a trail in the Region where the bucket exist will generate CloudTrail logs which you can stream to CloudWatch for viewing  and searching. CloudTrail event history does not log data events. Cloud Trail logs global service events by default, but S3 data events are not included_#0_+0_-0_%0\n" +
                        "EC2!Billing!Console_Which of the following Cost Explorer report types show you the monthly costs for your reserved EC2 instances?_$Cost and Usage reports_Reserved instance recommendations>Reserved Instances Coverage reports>Reserved Instances Utilization reports>Cost and Usage reports_@The cost and usage reports show you your monthly spend by service. The reserved instances reports and reserved instance recommendations do not show actual monthly costs._#0_+0_-0_%0\n" +
                        "Scenario!S3!CloudWatch!CloudTrail_You want to log every object downloaded from an S3 bucket in a specific region. You want to retain these logs indefinitely and search them easily. What is the most cost effective way to do this?_$Create a trail to log S3 data events_Use CloudTrail event history>Enable CloudTrail logging of global service events>Create a trail to log S3 data events_@Creating a trail in the Region where the bucket exist will generate CloudTrail logs which you can stream to CloudWatch for viewing  and searching. CloudTrail event history does not log data events. Cloud Trail logs global service events by default, but S3 data events are not included_#0_+0_-0_%0\n" +
                        "AWS!Lambda_What is the maximum time a Lambda function may run before timing out?_$15 minutes_5 minutes>1 minute>1 hour>15 minutes_@While the maximum time was at one point 5 minutes that has been changed to 15 minutes at this point._#0_+0_-0_%0\n" +
                        "Lightsail!Lambda_Which one of the following use container technologies?_$Docker_Lambda>Lightsail>Docker_@Both Lambda and Lightsail are compute services that, while they might possibly make use of containers under the hood are not themselves container technologies._#0_+0_-0_%0\n" +
                        "AWS!Lambda_What role can the Python programming language play in AWS Lambda?_$It can be set as the runtime environment for a function_Python cannot be used for Lambda>It is the primary language for API calls to administrate Lambda remotely>It is used as the underlying code driving the service>It can be set as the runtime environment for a function_@Python is indeed a valid choice for a functions runtime environment. There is no one primary language for Lambda API calls_#0_+0_-0_%0\n" +
                        "Lightsail!Database_Which one of the following AWS services is designed to let you deploy Docker containers?_$Elastic Container Services_Lightsail>Elastic Compute Cloud>Elastic Container Services_@While you could in theory at least manually install Docker Engine on either a Lightsail or EC2 instance, that is not their primary function._#0_+0_-0_%0\n" +
                        "Lightsail!Lambda_Which of the following use container technologies?_$Kubernetes_Lambda>Lightsail>Kubernetes_@Both Lambda and Lightsail are compute services that, while they might possibly make use of containers under the hood are not themselves container technologies._#0_+0_-0_%0\n" +
                        "Lightsail!EC2!EBS!RDS!Cloud_Which one of these AWS services use primarily EC2 resources under the hood?_$Lightsail_Elastic Block Store>Relational Database Service>Lightsail_@Elastic Block Store is for practical purposes an EC2 resource. RDS is largely built on its own infrastructure._#0_+0_-0_%0\n" +
                        "Beanstalk!Lightsail!Database_Which of the following AWS services is designed to let you deploy Docker containers?_$Elastic Beanstalk_Lightsail>Elastic Compute Cloud>Elastic Beanstalk_@While you could in theory at least manually install Docker Engine on either a Lightsail or EC2 instance, that is not their primary function._#0_+0_-0_%0\n" +
                        "AWS!Lightsail_Which one of these stacks are available from Lightsail blueprints?_$Gitlab_Ubuntu>WordPress>Gitlab_@Ubuntu is an OS not a stack. WordPress is an application not an OS._#0_+0_-0_%0\n" +
                        "Beanstalk!Lightsail!EC2!EBS!RDS!Cloud_Which of these AWS services use primarily EC2 resources under the hood?_$Elastic Beanstalk_Elastic Block Store>Relational Database Service>Elastic Beanstalk_@Elastic Block Store is for practical purposes an EC2 resource. REDS is largely built on its own infrastructure._#0_+0_-0_%0\n" +
                        "AWS!Lightsail_Which of these stacks are available from Lightsail blueprints?_$LAMP_Ubuntu>WordPress>LAMP_@Ubuntu is an OS not a stack. WordPress is an application not an OS._#0_+0_-0_%0\n" +
                        "EBS!EC2!Beanstalk!Lightsail_Which AWS service simplify the process of bringing web applications to deployment?_$Elastic Beanstalk_Elastic Block Store>Elastic Compute Cloud>Elastic Beanstalk_@Elastic Block Store provides storage volumes for Lightsail and Beanstalk also for EC2. Elastic Compute Cloud provides application deployment but no one ver accused it of being simple_#0_+0_-0_%0\n" +
                        "Lightsail!Beanstalk!EC2!RDS_Which of the following services bills at a flat rate regardless of how it is consumed?_$Lightsail_Elastic Beanstalk>Elastic Compute Cloud>Relational Database Service >Lightsail_@Beanstalk, EC2 and RDS all bill according to usage_#0_+0_-0_%0\n" +
                        "AWS!Scenario_Which one of the following use case is good candidates for spot instances?_$Big data processing workloads_Ecommerce websites>Long term, highly available, content rich websites>Big data processing workloads_@Because spot instances can be shut down, they are not recommended for applications that provide any kind of always on service._#0_+0_-0_%0\n" +
                        "EBS!EC2!Beanstalk!Lightsail_Which one of the following AWS services simplify the process of bringing web applications to deployment?_$Lightsail_Elastic Block Store>Elastic Compute Cloud>Lightsail_@Elastic Block Store provides storage volumes for Lightsail and Beanstalk also for EC2. Elastic Compute Cloud provides application deployment but no one ver accused it of being simple_#0_+0_-0_%0\n" +
                        "S3!Scenario!Storage!Glacier_Your budget conscious organization has a 5 TB database file it needs to retain off site for at least 5 years. In the event the organization needs to access the database it must be accessible within 8 hours. Which cloud storage option should you recommend?_$S3 Glacier_S3 has the most durable storage>S3>S3 has the fastest retrieval times>S3 does not support object sizes greater than 4TB>S3 Glacier_@Both S3 and Glacier are designed for durable long term storage and offer the same level of durability. Data stored in Glacier can be reliably retrieved within eight hours using the Expedited or Standard retrieval options. Data stored in S3 can be retrieved even faster than Glacier. S3 can store objects up to 5TB in size and Glacier can store archives up to 40TB. Both S3 or Glacier will meet the given requirements  but Glacier is the more cost effective solution._#0_+0_-0_%0\n" +
                        "AWS!Scenario_Which of the following use case is good candidates for spot instances?_$Continuous integration development environments_Ecommerce websites>Long term, highly available, content rich websites>Continuous integration development environments_@Because spot instances can be shut down, they are not recommended for applications that provide any kind of always on service._#0_+0_-0_%0\n" +
                        "S3!Scenario!Storage!Glacier_Your budget conscious organization has a 5 TB database file it needs to retain off site for at least 5 years. In the event the organization needs to access the database it must be accessible within 8 hours. Which cloud storage option should you recommend and why?_$Glacier is the most cost effective_S3 has the most durable storage>S3>S3 has the fastest retrieval times>S3 does not support object sizes greater than 4TB>Glacier is the most cost effective_@Both S3 and Glacier are designed for durable long term storage and offer the same level of durability. Data stored in Glacier can be reliably retrieved within eight hours using the Expedited or Standard retrieval options. Data stored in S3 can be retrieved even faster than Glacier. S3 can store objects up to 5TB in size and Glacier can store archives up to 40TB. Both S3 or Glacier will meet the given requirements  but Glacier is the more cost effective solution._#0_+0_-0_%0\n" +
                        "Glacier!Storage_Which of the following actions can you perform from the S3 Glacier service console?_$Create a vault_Delete an archive>create an archive>Delete a bucket>Retrieve an archive>Create a vault_@You can create or delete vaults from the Glacier service console. You cannot upload, download or delete archives. To perform archive actions you must use the AWS Command Line interface an AWS SDK or a third party program. Glacier does not use buckets _#0_+0_-0_%0\n" +
                        "Glacier!Storage_Which Glacier retrieval option generally takes 3 to 5 hours to complete?_$Standard_Provisioned>Expedited>Bulk>Standard_@The Standard retrieval option typically takes 3 to 5 hours to complete. Expedited takes 1 to 5 minutes, and Bulk takes 5 to 12 hours. There is no Provisioned retrieval option, but you can purchase provisioned capacity to ensure Expedited retrievals complete in a timely manner._#0_+0_-0_%0\n" +
                        "Glacier!Storage_Which one of the following types of AWS Storage Gateway lets you connect your servers to block storage using the iSCSI protocol?_$Tape gateway_Cached gateway>File gateway>Tape gateway_@The tape gateway and volume gateway types let you connect to iSCSI storage. The File gateway supports NFS. There is no such thing a s Cached gateway._#0_+0_-0_%0\n" +
                        "Scenario!Storage!S3_You need an easy way to transfer files from a server in your data center to S3 without having to install any third party software. Which of the following services and storage protocols could you use?_$AWS Storage Gateway with file gateway_AWS Snowball>The AWS CLI>AWS Storage Gateway with file gateway_@The AWS Storage Gateway allows transferring files from on premises servers to S3 using industry standard storage protocols. The AWS Storage Gateway functioning as a file gateway supports the SMB and NFS protocols. As a volume gateway it supports the iSCSI protocol. AWS Snowball and the AWS CLI also provide ways to transfer data to S3 but using them requires installing third party software_#0_+0_-0_%0\n" +
                        "Glacier!Storage_What is the minimum size for a Glacier archive?_$1 byte_40 TB>5 TB>0 bytes>1 byte_@A Glacier archive can be as small as 1 byte and as large as 40 TB. You cannot have a zero byte archive_#0_+0_-0_%0\n" +
                        "Snowball!Scenario!Storage!S3_You need an easy way to transfer files from a server in your data center to S3 without having to install any third party software. Which one of the following services and storage protocols could you use?_$iSCSI_AWS Snowball>The AWS CLI>iSCSI_@The AWS Storage Gateway allows transferring files from on premises servers to S3 using industry standard storage protocols. The AWS Storage Gateway functioning as a file gateway supports the SMB and NFS protocols. As a volume gateway it supports the iSCSI protocol. AWS Snowball and the AWS CLI also provide ways to transfer  data to S3 but using them requires installing third party software_#0_+0_-0_%0\n" +
                        "Glacier!Storage_Which type of AWS Storage Gateway lets you connect your servers to block storage using the iSCSI protocol?_$Volume gateway_Cached gateway>File gateway>Volume gateway_@The tape gateway and volume gateway types let you connect to iSCSI storage. The File gateway supports NFS. There is no such thing a s Cached gateway._#0_+0_-0_%0\n" +
                        "Snowball!Scenario!Storage!S3_You need an easy way to transfer files from a server in your data center to S3 without having to install any third party software. Which of the following services and storage protocols could you use to do this?_$SMB or Server Message Block_AWS Snowball>The AWS CLI>SMB or Server Message Block_@The AWS Storage Gateway allows transferring files from on premises servers to S3 using industry standard storage protocols. The AWS Storage Gateway functioning as a file gateway supports the SMB and NFS protocols. As a volume gateway it supports the iSCSI protocol. AWS Snowball and the AWS CLI also provide ways to transfer  data to S3 but using them requires installing third party software_#0_+0_-0_%0\n" +
                        "AWS!Storage_Which of the following are true regarding the AWS Storage Gateway with volume gateway configuration?_$Stored volumes asynchronously back up data to S3 as EBS snapshots_Stored volumes can be up to 32 TB in size>Cached volumes asynchronously back up data to S3 as EBS snapshots.>Stored volumes asynchronously back up data to S3 as EBS snapshots_@The volume gateway type offers two configurations which are stored volumes and cached volumes. Stored volumes store all data locally and asynchronously back up that data to S3 as EBS snapshots. Stored volumes can be up to 16 TB in size. In contrast , cached volumes locally store only a frequently used subset of data but do not asynchronously back up the data to S3 as EBS snapshots. Cache volumes can be up to 32 TB in size_#0_+0_-0_%0\n" +
                        "AWS!Storage_Where does AWS Storage Gateway primarily store data?_$S3 buckets_Glacier vaults>EBS volumes>EBS snapshots>S3 buckets_@All AWS Storage Gateway types such as file volume and tape gateways primarily store in S3 buckets. From there data can be stored in Glacier or EBS snapshots, which can be instantiated as EBS volumes_#0_+0_-0_%0\n" +
                        "AWS!Storage_Which one of the following are true regarding the AWS Storage Gateway with volume gateway configuration?_$Cached volumes locally store only a frequently used subset of data_Stored volumes can be up to 32 TB in size>Cached volumes asynchronously back up data to S3 as EBS snapshots.>Cached volumes locally store only a frequently used subset of data_@The volume gateway type offers two configurations which are stored volumes and cached volumes. Stored volumes store all data locally and asynchronously bak up that data to S3 as EBS snapshots. Stored volumes can be up to 16 TB in size. In contrast , cached volumes locally store only a frequently used subset of data but do not asynchronously back up the data to S3 as EBS snapshots. Cache volumes can be up to 32 TB in size_#0_+0_-0_%0\n" +
                        "Snowball!Scenario!Storage!S3_You need an easy way to transfer files from a server in your data center to S3 without having to install any third party software. Which of the following services and storage protocols could you use to do that?_$AWS Storage Gateway with volume gateway_AWS Snowball>The AWS CLI>AWS Storage Gateway with volume gateway_@The AWS Storage Gateway allows transferring files from on premises servers to S3 using industry standard storage protocols. The AWS Storage Gateway functioning as a file gateway supports the SMB and NFS protocols. As a volume gateway it supports the iSCSI protocol. AWS Snowball and the AWS CLI also provide ways to transfer  data to S3 but using them requires installing third party software_#0_+0_-0_%0\n" +
                        "AWS!Storage_Which of the following is true regarding the AWS Storage Gateway with volume gateway configuration?_$Cached volumes can be up to 32 TB in size_Stored volumes can be up to 32 TB in size>Cached volumes asynchronously back up data to S3 as EBS snapshots.>Cached volumes can be up to 32 TB in size_@The volume gateway type offers two configurations which are stored volumes and cached volumes. Stored volumes store all data locally and asynchronously bak up that data to S3 as EBS snapshots. Stored volumes can be up to 16 TB in size. In contrast , cached volumes locally store only a frequently used subset of data but do not asynchronously back up the data to S3 as EBS snapshots. Cache volumes can be up to 32 TB in size_#0_+0_-0_%0\n" +
                        "Snowball!Storage_Which one of the following are security features of AWS Snowball?_$It enforces encryption at rest_It enforces NFC encryption>It has tamper resistant network ports>It enforces encryption at rest_@AWS Snowball enforces encryption at rest and in transit. It also uses TPM chip to detect unauthorized changes to the hardware or software. Snow ball does not use NFS encryption and it does not have a tamper resistant network ports_#0_+0_-0_%0\n" +
                        "Snowball!Storage_What is the most data you can store on a single Snowball device?_$72 TB_42 TB>50 TB>80 TB>72 TB_@The 80 TB Snowball device offers 72 TB of usable storage and is the largest available. The 50 TB Snowball offers 42 TB of usable space_#0_+0_-0_%0\n" +
                        "Snowball!Storage_Which of the following are security features of AWS Snowball?_$It uses Trusted Platform Module chip_It enforces NFC encryption>It has tamper resistant network ports>It uses Trusted Platform Module chip_@AWS Snowball enforces encryption at rest and in transit. It also uses TPM chip to detect unauthorized changes to the hardware or software. Snow ball does not use NFS encryption and it does not have a tamper resistant network ports_#0_+0_-0_%0\n" +
                        "Database!RDS!DynamoDB_Which of the following is a database engine option for Amazon Relational Database Service?_$PostgreSQL_IBM dBase>DynamoDB>Redis>PostgreSQL_@PostgreSQL and Amazon Aurora are options for RDS database engines. IBM dBase and the other non relational databases DynamoDB and Redis are not available as RDS database engines_#0_+0_-0_%0\n" +
                        "Database!RDS!DynamoDB_What do new Relational Database Service instances use for database storage?_$Elastic Block Store_Instance volumes>Snapshots>Magnetic storage>Elastic Block Store_@RDS instances use EBS volumes for storage. They no longer can use magnetic storage. Instance volumes are temporary not database storage. You can take a snapshot of a database instance and restore it to  a new instance with a new EBS volume, but an RDS instance cannot use a snapshot directly for database storage_#0_+0_-0_%0\n" +
                        "AWS!Database_Which one of these databases is Amazon Aurora compatible with?_$MySQL_MariaDB>Oracle>Microsoft SQL server>MySQL_@Aurora is Amazons proprietary database engine that works with existing PostgreSQL and MySQL databases. Aurora does not support MariaDB Oracle or Microsoft SQL Server_#0_+0_-0_%0\n" +
                        "Database!RDS!DynamoDB_Which one of the following is a database engine option for Amazon Relational Database Service?_$Amazon Aurora_IBM dBase>DynamoDB>Redis>Amazon Aurora_@PostgreSQL and Amazon Aurora are options for RDS database engines. IBM dBase and the other non relational databases DynamoDB and Redis are not available as RDS database engines_#0_+0_-0_%0\n" +
                        "AWS!Database_Which one database is Amazon Aurora compatible with?_$PostgreSQL_MariaDB>Oracle>Microsoft SQL server>PostgreSQL_@Aurora is Amazons proprietary database engine that works with existing PostgreSQL and MySQL databases. Aurora does not support MariaDB Oracle or Microsoft SQL Server_#0_+0_-0_%0\n" +
                        "Cloud!CloudFront_On how many continents are CloudFront edge locations distributed?_$6_7>5>4>6_@CloudFront has edge locations on six continent_#0_+0_-0_%0\n" +
                        "Cloud!CloudFront_From where does CloudFront retrieve content to store for caching?_$Origins_Regions>Distributions>Edge locations>Origins_@A CloudFront origin is the location that a distribution sources content from. Content is stored in edge locations. A distribution defines the edge locations and origins to use_#0_+0_-0_%0\n" +
                        "AWS!Console!Account_What Auto Scaling group parameter sets the limit for the number of instances that Auto Scaling creates?_$Maximum_Group size>Desired capacity>Maximum_@The maximum and minimum group size values limit the number of instances in an Auto Scaling group. The desired capacity is the number of instances that the Auto Scaling group will generally maintain, but Auto Scaling can launch or terminate instances if dynamic scaling calls for it._#0_+0_-0_%0\n" +
                        "Cloud!CloudFront_Which CloudFront distribution type requires you to provide a media player?_$RTMP_Streaming>Web>Edge>RTMP_@The RTMP distribution type is for delivering streaming content and requires you to provide a media player. A Web distribution can also stream audio or video content but does not require a media player. Streaming and Edge are not distribution types._#0_+0_-0_%0\n" +
                        "AWS!Console!Account_Which one of these Auto Scaling group parameters set the limit for the number of instances that Auto Scaling creates?_$Minimum_Group size>Desired capacity>Minimum_@The maximum and minimum group size values limit the number of instances in an Auto Scaling group. The desired capacity is the number of instances that the Auto Scaling group will generally maintain, but Auto Scaling can launch or terminate instances if dynamic scaling calls for it._#0_+0_-0_%0\n" +
                        "AWS!Console!Account_What Auto Scaling feature creates a scaling schedule based on past usage patterns?_$Predictive scaling_Scheduled scaling>Dynamic scaling>Pattern Scaling>Predictive scaling_@Predictive scaling creates a scheduled scaling action based on past usage patterns. Scheduled scaling and dynamic scaling do not create scheduled scaling actions. There is no such thing as pattern scaling_#0_+0_-0_%0\n" +
                        "Scenario!S3_You are hosting a static website on S3. Your web assets are under the Standard storage class. Which of the following is true regarding your site?_$You are responsible for S3 charges_Someone may modify the content of your site without authorization>You are charged for any compute power used to host the site>An Availability Zone outage may bring down the site>You are responsible for S3 charges_@You are responsible for S3 charges related to your static website. You are not charged for compute with S3. No one may modify the content of your site unless you give them permission. The S3 Standard storage class keeps objects in multiple Availability Zones so the outage of one will not affect the site._#0_+0_-0_%0\n" +
                        "EC2!S3!VPC_An Auto Scaling group can use an EC2 system health check to determine whether an instance is healthy. What other type of health check can it use?_$ELB or Elastic Load Balancing_S3 or Simple Storage Service>SNS or Simple Notification Service>VPC or Virtual Private Cloud>ELB or Elastic Load Balancing_@An Auto Scaling group can use an ELB health check to determine whether an instance is healthy. There is no such thing as an S3 health check, A VPC health check, or an SNS health check._#0_+0_-0_%0\n" +
                        "EC2!Scenario_You have a public web application running on EC2 instances. Which of the following factors affecting the performance of your application might be out of your control?_$Network_Storage>Compute>Database>Network_@You may have control over your VPC but the rest of the network between your application and users on the internet is not under your control. Compute, storage, and any database your application use are or at least theoretically could be under your control_#0_+0_-0_%0\n" +
                        "S3!Storage_Which one of the following features of S3 improve the security of data you store in an S3 bucket?_$Objects in S3 are not public by default_All objects are readable by all AWS users by default>S3 removes public objects by default>Objects in S3 are not public by default_@Objects you upload to an S3 bucket are not public by default, nor are they accessible to all AWS users. Even if you try to make an object public using ACL, S3 will immediately remove the ACL, but you can disable this behavior. S3 never removes objects by default._#0_+0_-0_%0\n" +
                        "Scenario!EC2_Which of the following can impact the reliability of a web application running on EC2 instances?_$Not replacing a misconfigured resource that the application depends on _Taking EBS snapshots of the instances>The user interface is too difficult to use>Provisioning too many instances>Not replacing a misconfigured resource that the application depends on _@The reliability of an application can be impacted by the failure of resources that the application depends on. One way a resource can fail is if it is misconfigured. Taking EBS snapshots of an instance or provisioning more instances that you need will not impact reliability. The user interface being difficult to use might be an annoyance for the user but does not affect the actual reliability of the application._#0_+0_-0_%0\n" +
                        "S3!Storage_Which of the following is required to enable S3 static web hosting on a bucket?_$Enable bucket hosting in the S3 service console_Disable default encryption>Disable object versioning>Enable object versioning>Make all objects in the bucket public>Enable bucket hosting in the S3 service console_@To have S3 host your static website, you need to enable bucket hosting in the S3 service console. It is not necessary to disable or enable default encryption or object versioning. There is also no need to make all objects in the bucket public, but only those that want S3 to serve up._#0_+0_-0_%0\n" +
                        "S3!Storage_Which of the following features of S3 improve the security of data you store in an S3 bucket?_$By default, S3 removes ACLs that allow public read access to objects_All objects are readable by all AWS users by default>S3 removes public objects by default>By default, S3 removes ACLs that allow public read access to objects_@Objects you upload to an S3 bucket are not public by default, nor are they accessible to all AWS users. Even if you try to make an object public using ACL, S3 will immediately remove the ACL, but you can disable this behavior. S3 never removes objects by default._#0_+0_-0_%0";
        return questioningss;
    }

}

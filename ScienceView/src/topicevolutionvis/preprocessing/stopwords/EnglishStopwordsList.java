package topicevolutionvis.preprocessing.stopwords;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents the English (default) stopwords list.
 * 
 * @author Fernando Vieira Paulovich
 */
public class EnglishStopwordsList extends StopwordsList implements Cloneable {

    public EnglishStopwordsList() {
        try {
            this.name = "English (default)";
            this.fill();
        } catch (IOException ex) {
            Logger.getLogger(EnglishStopwordsList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void fill() throws IOException {
        this.stopwords = new ArrayList<>();
        this.stopwords.add("a");
        this.stopwords.add("able");
        this.stopwords.add("about");
        this.stopwords.add("above");
        this.stopwords.add("according");
        this.stopwords.add("accordingly");
        this.stopwords.add("across");
        this.stopwords.add("actually");
        this.stopwords.add("after");
        this.stopwords.add("afterwards");
        this.stopwords.add("again");
        this.stopwords.add("against");
        this.stopwords.add("all");
        this.stopwords.add("allow");
        this.stopwords.add("allows");
        this.stopwords.add("almost");
        this.stopwords.add("alone");
        this.stopwords.add("along");
        this.stopwords.add("already");
        this.stopwords.add("also");
        this.stopwords.add("although");
        this.stopwords.add("always");
        this.stopwords.add("am");
        this.stopwords.add("among");
        this.stopwords.add("amongst");
        this.stopwords.add("an");
        this.stopwords.add("and");
        this.stopwords.add("another");
        this.stopwords.add("any");
        this.stopwords.add("anybody");
        this.stopwords.add("anyhow");
        this.stopwords.add("anyone");
        this.stopwords.add("anything");
        this.stopwords.add("anyway");
        this.stopwords.add("anyways");
        this.stopwords.add("anywhere");
        this.stopwords.add("apart");
        this.stopwords.add("appear");
        this.stopwords.add("appreciate");
        this.stopwords.add("appropriate");
        this.stopwords.add("are");
        this.stopwords.add("around");
        this.stopwords.add("as");
        this.stopwords.add("aside");
        this.stopwords.add("ask");
        this.stopwords.add("asking");
        this.stopwords.add("associated");
        this.stopwords.add("at");
        this.stopwords.add("available");
        this.stopwords.add("away");
        this.stopwords.add("awfully");
        this.stopwords.add("b");
        this.stopwords.add("be");
        this.stopwords.add("became");
        this.stopwords.add("because");
        this.stopwords.add("become");
        this.stopwords.add("becomes");
        this.stopwords.add("becoming");
        this.stopwords.add("been");
        this.stopwords.add("before");
        this.stopwords.add("beforehand");
        this.stopwords.add("behind");
        this.stopwords.add("being");
        this.stopwords.add("believe");
        this.stopwords.add("below");
        this.stopwords.add("beside");
        this.stopwords.add("besides");
        this.stopwords.add("best");
        this.stopwords.add("better");
        this.stopwords.add("between");
        this.stopwords.add("beyond");
        this.stopwords.add("both");
        this.stopwords.add("brief");
        this.stopwords.add("but");
        this.stopwords.add("by");
        this.stopwords.add("c");
        this.stopwords.add("came");
        this.stopwords.add("can");
        this.stopwords.add("cannot");
        this.stopwords.add("cant");
        this.stopwords.add("cause");
        this.stopwords.add("causes");
        this.stopwords.add("certain");
        this.stopwords.add("certainly");
        this.stopwords.add("changes");
        this.stopwords.add("clearly");
        this.stopwords.add("co");
        this.stopwords.add("com");
        this.stopwords.add("come");
        this.stopwords.add("comes");
        this.stopwords.add("concerning");
        this.stopwords.add("consequently");
        this.stopwords.add("consider");
        this.stopwords.add("considering");
        this.stopwords.add("contain");
        this.stopwords.add("containing");
        this.stopwords.add("contains");
        this.stopwords.add("corresponding");
        this.stopwords.add("could");
        this.stopwords.add("course");
        this.stopwords.add("currently");
        this.stopwords.add("d");
        this.stopwords.add("definitely");
        this.stopwords.add("described");
        this.stopwords.add("despite");
        this.stopwords.add("did");
        this.stopwords.add("different");
        this.stopwords.add("do");
        this.stopwords.add("does");
        this.stopwords.add("doing");
        this.stopwords.add("done");
        this.stopwords.add("down");
        this.stopwords.add("downwards");
        this.stopwords.add("during");
        this.stopwords.add("e");
        this.stopwords.add("each");
        this.stopwords.add("eds");
        this.stopwords.add("edu");
        this.stopwords.add("eg");
        this.stopwords.add("eight");
        this.stopwords.add("either");
        this.stopwords.add("else");
        this.stopwords.add("elsewhere");
        this.stopwords.add("enough");
        this.stopwords.add("entirely");
        this.stopwords.add("especially");
        this.stopwords.add("et");
        this.stopwords.add("etc");
        this.stopwords.add("even");
        this.stopwords.add("ever");
        this.stopwords.add("every");
        this.stopwords.add("everybody");
        this.stopwords.add("everyone");
        this.stopwords.add("everything");
        this.stopwords.add("everywhere");
        this.stopwords.add("ex");
        this.stopwords.add("exactly");
        this.stopwords.add("example");
        this.stopwords.add("except");
        this.stopwords.add("f");
        this.stopwords.add("far");
        this.stopwords.add("few");
        this.stopwords.add("fifth");
        this.stopwords.add("first");
        this.stopwords.add("five");
        this.stopwords.add("followed");
        this.stopwords.add("following");
        this.stopwords.add("follows");
        this.stopwords.add("for");
        this.stopwords.add("former");
        this.stopwords.add("formerly");
        this.stopwords.add("forth");
        this.stopwords.add("four");
        this.stopwords.add("from");
        this.stopwords.add("further");
        this.stopwords.add("furthermore");
        this.stopwords.add("g");
        this.stopwords.add("get");
        this.stopwords.add("gets");
        this.stopwords.add("getting");
        this.stopwords.add("given");
        this.stopwords.add("gives");
        this.stopwords.add("go");
        this.stopwords.add("goes");
        this.stopwords.add("going");
        this.stopwords.add("gone");
        this.stopwords.add("got");
        this.stopwords.add("gotten");
        this.stopwords.add("greetings");
        this.stopwords.add("h");
        this.stopwords.add("had");
        this.stopwords.add("happens");
        this.stopwords.add("hardly");
        this.stopwords.add("has");
        this.stopwords.add("have");
        this.stopwords.add("having");
        this.stopwords.add("he");
        this.stopwords.add("hello");
        this.stopwords.add("help");
        this.stopwords.add("hence");
        this.stopwords.add("her");
        this.stopwords.add("here");
        this.stopwords.add("hereafter");
        this.stopwords.add("hereby");
        this.stopwords.add("herein");
        this.stopwords.add("hereupon");
        this.stopwords.add("hers");
        this.stopwords.add("herself");
        this.stopwords.add("hi");
        this.stopwords.add("him");
        this.stopwords.add("himself");
        this.stopwords.add("his");
        this.stopwords.add("hither");
        this.stopwords.add("hopefully");
        this.stopwords.add("how");
        this.stopwords.add("howbeit");
        this.stopwords.add("however");
        this.stopwords.add("i");
        this.stopwords.add("ie");
        this.stopwords.add("if");
        this.stopwords.add("ignored");
        this.stopwords.add("immediate");
        this.stopwords.add("in");
        this.stopwords.add("inasmuch");
        this.stopwords.add("inc");
        this.stopwords.add("indeed");
        this.stopwords.add("indicate");
        this.stopwords.add("indicated");
        this.stopwords.add("indicates");
        this.stopwords.add("inner");
        this.stopwords.add("insofar");
        this.stopwords.add("instead");
        this.stopwords.add("into");
        this.stopwords.add("inward");
        this.stopwords.add("is");
        this.stopwords.add("it");
        this.stopwords.add("its");
        this.stopwords.add("itself");
        this.stopwords.add("j");
        this.stopwords.add("just");
        this.stopwords.add("k");
        this.stopwords.add("keep");
        this.stopwords.add("keeps");
        this.stopwords.add("kept");
        this.stopwords.add("know");
        this.stopwords.add("known");
        this.stopwords.add("knows");
        this.stopwords.add("l");
        this.stopwords.add("last");
        this.stopwords.add("lately");
        this.stopwords.add("later");
        this.stopwords.add("latter");
        this.stopwords.add("latterly");
        this.stopwords.add("least");
        this.stopwords.add("less");
        this.stopwords.add("lest");
        this.stopwords.add("let");
        this.stopwords.add("like");
        this.stopwords.add("liked");
        this.stopwords.add("likely");
        this.stopwords.add("little");
        this.stopwords.add("look");
        this.stopwords.add("looking");
        this.stopwords.add("looks");
        this.stopwords.add("ltd");
        this.stopwords.add("m");
        this.stopwords.add("mainly");
        this.stopwords.add("many");
        this.stopwords.add("may");
        this.stopwords.add("maybe");
        this.stopwords.add("me");
        this.stopwords.add("mean");
        this.stopwords.add("meanwhile");
        this.stopwords.add("merely");
        this.stopwords.add("might");
        this.stopwords.add("more");
        this.stopwords.add("moreover");
        this.stopwords.add("most");
        this.stopwords.add("mostly");
        this.stopwords.add("much");
        this.stopwords.add("must");
        this.stopwords.add("my");
        this.stopwords.add("myself");
        this.stopwords.add("n");
        this.stopwords.add("name");
        this.stopwords.add("namely");
        this.stopwords.add("nd");
        this.stopwords.add("near");
        this.stopwords.add("nearly");
        this.stopwords.add("necessary");
        this.stopwords.add("need");
        this.stopwords.add("needs");
        this.stopwords.add("neither");
        this.stopwords.add("never");
        this.stopwords.add("nevertheless");
        this.stopwords.add("new");
        this.stopwords.add("next");
        this.stopwords.add("nine");
        this.stopwords.add("no");
        this.stopwords.add("nobody");
        this.stopwords.add("non");
        this.stopwords.add("none");
        this.stopwords.add("noone");
        this.stopwords.add("nor");
        this.stopwords.add("normally");
        this.stopwords.add("not");
        this.stopwords.add("nothing");
        this.stopwords.add("novel");
        this.stopwords.add("now");
        this.stopwords.add("nowhere");
        this.stopwords.add("o");
        this.stopwords.add("obviously");
        this.stopwords.add("of");
        this.stopwords.add("off");
        this.stopwords.add("often");
        this.stopwords.add("oh");
        this.stopwords.add("ok");
        this.stopwords.add("okay");
        this.stopwords.add("old");
        this.stopwords.add("on");
        this.stopwords.add("once");
        this.stopwords.add("one");
        this.stopwords.add("ones");
        this.stopwords.add("only");
        this.stopwords.add("onto");
        this.stopwords.add("or");
        this.stopwords.add("other");
        this.stopwords.add("others");
        this.stopwords.add("otherwise");
        this.stopwords.add("ought");
        this.stopwords.add("our");
        this.stopwords.add("ours");
        this.stopwords.add("ourselves");
        this.stopwords.add("out");
        this.stopwords.add("outside");
        this.stopwords.add("over");
        this.stopwords.add("overall");
        this.stopwords.add("own");
        this.stopwords.add("p");
        this.stopwords.add("particular");
        this.stopwords.add("particularly");
        this.stopwords.add("per");
        this.stopwords.add("perhaps");
        this.stopwords.add("placed");
        this.stopwords.add("please");
        this.stopwords.add("plus");
        this.stopwords.add("possible");
        this.stopwords.add("presumably");
        this.stopwords.add("probably");
        this.stopwords.add("provides");
        this.stopwords.add("q");
        this.stopwords.add("que");
        this.stopwords.add("quite");
        this.stopwords.add("qv");
        this.stopwords.add("r");
        this.stopwords.add("rather");
        this.stopwords.add("rd");
        this.stopwords.add("re");
        this.stopwords.add("really");
        this.stopwords.add("reasonably");
        this.stopwords.add("regarding");
        this.stopwords.add("regardless");
        this.stopwords.add("regards");
        this.stopwords.add("relatively");
        this.stopwords.add("respectively");
        this.stopwords.add("right");
        this.stopwords.add("s");
        this.stopwords.add("said");
        this.stopwords.add("same");
        this.stopwords.add("saw");
        this.stopwords.add("say");
        this.stopwords.add("saying");
        this.stopwords.add("says");
        this.stopwords.add("second");
        this.stopwords.add("secondly");
        this.stopwords.add("see");
        this.stopwords.add("seeing");
        this.stopwords.add("seem");
        this.stopwords.add("seemed");
        this.stopwords.add("seeming");
        this.stopwords.add("seems");
        this.stopwords.add("seen");
        this.stopwords.add("self");
        this.stopwords.add("selves");
        this.stopwords.add("sensible");
        this.stopwords.add("sent");
        this.stopwords.add("serious");
        this.stopwords.add("seriously");
        this.stopwords.add("seven");
        this.stopwords.add("several");
        this.stopwords.add("shall");
        this.stopwords.add("she");
        this.stopwords.add("should");
        this.stopwords.add("since");
        this.stopwords.add("six");
        this.stopwords.add("so");
        this.stopwords.add("some");
        this.stopwords.add("somebody");
        this.stopwords.add("somehow");
        this.stopwords.add("someone");
        this.stopwords.add("something");
        this.stopwords.add("sometime");
        this.stopwords.add("sometimes");
        this.stopwords.add("somewhat");
        this.stopwords.add("somewhere");
        this.stopwords.add("soon");
        this.stopwords.add("sorry");
        this.stopwords.add("specified");
        this.stopwords.add("specify");
        this.stopwords.add("specifying");
        this.stopwords.add("still");
        this.stopwords.add("sub");
        this.stopwords.add("such");
        this.stopwords.add("sup");
        this.stopwords.add("sure");
        this.stopwords.add("t");
        this.stopwords.add("take");
        this.stopwords.add("taken");
        this.stopwords.add("tell");
        this.stopwords.add("tends");
        this.stopwords.add("th");
        this.stopwords.add("than");
        this.stopwords.add("thank");
        this.stopwords.add("thanks");
        this.stopwords.add("thanx");
        this.stopwords.add("that");
        this.stopwords.add("thats");
        this.stopwords.add("the");
        this.stopwords.add("their");
        this.stopwords.add("theirs");
        this.stopwords.add("them");
        this.stopwords.add("themselves");
        this.stopwords.add("then");
        this.stopwords.add("thence");
        this.stopwords.add("there");
        this.stopwords.add("thereafter");
        this.stopwords.add("thereby");
        this.stopwords.add("therefore");
        this.stopwords.add("therein");
        this.stopwords.add("theres");
        this.stopwords.add("thereupon");
        this.stopwords.add("these");
        this.stopwords.add("they");
        this.stopwords.add("think");
        this.stopwords.add("third");
        this.stopwords.add("this");
        this.stopwords.add("thorough");
        this.stopwords.add("thoroughly");
        this.stopwords.add("those");
        this.stopwords.add("though");
        this.stopwords.add("three");
        this.stopwords.add("through");
        this.stopwords.add("throughout");
        this.stopwords.add("thru");
        this.stopwords.add("thus");
        this.stopwords.add("to");
        this.stopwords.add("together");
        this.stopwords.add("too");
        this.stopwords.add("took");
        this.stopwords.add("toward");
        this.stopwords.add("towards");
        this.stopwords.add("tried");
        this.stopwords.add("tries");
        this.stopwords.add("truly");
        this.stopwords.add("try");
        this.stopwords.add("trying");
        this.stopwords.add("twice");
        this.stopwords.add("two");
        this.stopwords.add("u");
        this.stopwords.add("un");
        this.stopwords.add("under");
        this.stopwords.add("unfortunately");
        this.stopwords.add("unless");
        this.stopwords.add("unlikely");
        this.stopwords.add("until");
        this.stopwords.add("unto");
        this.stopwords.add("up");
        this.stopwords.add("upon");
        this.stopwords.add("us");
        this.stopwords.add("use");
        this.stopwords.add("used");
        this.stopwords.add("useful");
        this.stopwords.add("uses");
        this.stopwords.add("using");
        this.stopwords.add("usually");
        this.stopwords.add("uucp");
        this.stopwords.add("v");
        this.stopwords.add("value");
        this.stopwords.add("various");
        this.stopwords.add("very");
        this.stopwords.add("via");
        this.stopwords.add("viz");
        this.stopwords.add("vs");
        this.stopwords.add("w");
        this.stopwords.add("want");
        this.stopwords.add("wants");
        this.stopwords.add("was");
        this.stopwords.add("way");
        this.stopwords.add("we");
        this.stopwords.add("welcome");
        this.stopwords.add("well");
        this.stopwords.add("went");
        this.stopwords.add("were");
        this.stopwords.add("what");
        this.stopwords.add("whatever");
        this.stopwords.add("when");
        this.stopwords.add("whence");
        this.stopwords.add("whenever");
        this.stopwords.add("where");
        this.stopwords.add("whereafter");
        this.stopwords.add("whereas");
        this.stopwords.add("whereby");
        this.stopwords.add("wherein");
        this.stopwords.add("whereupon");
        this.stopwords.add("wherever");
        this.stopwords.add("whether");
        this.stopwords.add("which");
        this.stopwords.add("while");
        this.stopwords.add("whither");
        this.stopwords.add("who");
        this.stopwords.add("whoever");
        this.stopwords.add("whole");
        this.stopwords.add("whom");
        this.stopwords.add("whose");
        this.stopwords.add("why");
        this.stopwords.add("will");
        this.stopwords.add("willing");
        this.stopwords.add("wish");
        this.stopwords.add("with");
        this.stopwords.add("within");
        this.stopwords.add("without");
        this.stopwords.add("wonder");
        this.stopwords.add("would");
        this.stopwords.add("x");
        this.stopwords.add("y");
        this.stopwords.add("yes");
        this.stopwords.add("yet");
        this.stopwords.add("you");
        this.stopwords.add("your");
        this.stopwords.add("yours");
        this.stopwords.add("yourself");
        this.stopwords.add("yourselves");
        this.stopwords.add("z");
        this.stopwords.add("zero");
        Collections.sort(this.stopwords);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        StopwordsList clone = new EnglishStopwordsList();

        clone.name = this.name;
        clone.stopwords = (ArrayList<String>) this.stopwords.clone();

        return clone;
    }

}

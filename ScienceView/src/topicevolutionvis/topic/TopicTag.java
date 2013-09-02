/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.topic;

/**
 *
 * @author Aretha
 */
public class TopicTag implements Comparable<TopicTag> {

    public String term;
    public double value;

    public TopicTag(String term, double value) {
        this.term = term;
        this.value = value;

    }

    @Override
    public int compareTo(TopicTag o) {
        return (int) (o.value - this.value);
    }
}

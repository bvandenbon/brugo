package brugo.go.io.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import brugo.go.bo.state.Intersection;
import brugo.go.io.sgf.SGFEncoder;

public class IntersectionSGFJavaTypeAdapter extends XmlAdapter<String, Intersection>
{
  SGFEncoder encoder = new SGFEncoder();

  @Override
  public Intersection unmarshal(String v) throws Exception
  {
    return encoder.fromSGF(v);
  }

  @Override
  public String marshal(Intersection v) throws Exception
  {
    return encoder.toSGF(v);
  }
}

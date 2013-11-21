package hu.sztaki.phytree.io;


import static org.junit.Assert.*;

import hu.sztaki.phytree.FastaItem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class FastaReaderTest {

  private final String oneShortItem1 = ">sp|Q6GZX4|001R_FRG3G Putative "
      + "transcription factor 001R OS=Frog virus 3 (isolate Goorha) "
      + "GN=FV3-001R PE=4 SV=1\n"
      + "MAFSAEDVLKEYDRRRRMEALLLSLYYPNDRKLLDYKEWSPPRVQVECPKAPVEWNNPPS\n"
      + "EKGLIVGHFSGIKYKGEKAQASEVDVNKMCCWVSKFKDAMRRYQGIQTCKIPGKVLSDLD\n"
      + "AKIKAYNLTVEGVEGFVRYSRVTKQHVAAFLKELRHSKQYENVNLIHYILTDKRVDIQHL\n"
      + "EKDLVKDFKALVESAHRMRQGHMINVKYILYQLLKKHGHGPDGPDILTVKTGSKGVLYDD\n"
      + "SFRKIYTDLGWKFTP\n";

  private final String oneShortItem2 = ">sp|Q6GZX3|001R_FRG3G Putative "
      + "transcription factor 001R OS=Frog virus 3 (isolate Goorha) "
      + "GN=FV3-001R PE=4 SV=1\n"
      + "MSIIGATRLQNDKSDTYSAGPCYAGGCSAFTPRGTCGKDWDLGEQTCASGFCTSQPLCAR\n"
      + "IKKTQVCGLRYSSKGKDPLVSAEWDSRGAPYVRCTYDADLIDTQAQVDQFVSMFGESPSL\n"
      + "IKKTQVCGLRYSSKGKDPLVSAEWDSRGAPYVRCTYDADLIDTQAQVDQFVSMFGESPSL\n"
      + "KCINRASDPVYQKVKTLHAYPDQCWYVPCAADVGELKMGTQRDTPTNCPTQVCQIVFNML\n"
      + "DDGSVTMDDVKNTINCDFSKYVPPPPPPKPTPPTPPTPPTPPTPPTPPTPPTPRPVHNRK\n"
      + "VMFFVAGAVLVAILISTVRW\n";

  private final String oneShortItem3 = ">sp|Q197F8|002R_IIV3 Uncharacterized "
      + "protein 002R OS=Invertebrate iridescent virus 3 GN=IIV3-002R "
      + "PE=4 SV=1\n"
      + "MASNTVSAQGGSNRPVRDFSNIQDVAQFLLFDPIWNEQPGSIVPWKMNREQALAERYPEL\n"
      + "QTSEPSEDYSGPVESLELLPLEIKLDIMQYLSWEQISWCKHPWLWTRWYKDNVVRVSAIT\n"
      + "FEDFQREYAFPEKIQEIHFTDTRAEEIKAILETTPNVTRLVIRRIDDMNYNTHGDLGLDD\n"
      + "LEFLTHLMVEDACGFTDFWAPSLTHLTIKNLDMHPRWFGPVMDGIKSMQSTLKYLYIFET\n"
      + "YGVNKPFVQWCTDNIETFYCTNSYRYENVPRPIYVWVLFQEDEWHGYRVEDNKFHRRYMY\n"
      + "STILHKRDTDWVENNPLKTPAQVEMYKFLLRISQLNRDGTGYESDSDPENEHFDDESFSS\n"
      + "GEEDSSDEDDPTWAPDSDDSDWETETEEEPSVAARILEKGKLTITNLMKSLGFKPKPKKI\n"
      + "QSIDRYFCSLDSNYNSEDEDFEYDSDSEDDDSDSEDDC\n";

  private InputStream isAll;

  @Before
  public void setUp() {
    isAll = new ByteArrayInputStream(
        (oneShortItem1 + oneShortItem2 + oneShortItem3).getBytes());
  }

  @Test
  public void testGetNextFastaItem() {
    FastaReader fastaReader = new FastaReader(isAll);
    String headerRow = ">sp|Q6GZX4|001R_FRG3G Putative "
        + "transcription factor 001R OS=Frog virus 3 (isolate Goorha) "
        + "GN=FV3-001R PE=4 SV=1";
    String sequence = "MAFSAEDVLKEYDRRRRMEALLLSLYYPNDRKLLDYKEWSPPRVQVECPKAPVE"
        + "WNNPPS"
        + "EKGLIVGHFSGIKYKGEKAQASEVDVNKMCCWVSKFKDAMRRYQGIQTCKIPGKVLSDLD"
        + "AKIKAYNLTVEGVEGFVRYSRVTKQHVAAFLKELRHSKQYENVNLIHYILTDKRVDIQHL"
        + "EKDLVKDFKALVESAHRMRQGHMINVKYILYQLLKKHGHGPDGPDILTVKTGSKGVLYDD"
        + "SFRKIYTDLGWKFTP";
    try {
      FastaItem fastaItem = fastaReader.getNextFastaItem();
      assertEquals("Q6GZX4", fastaItem.getAcNum());
      assertEquals(headerRow, fastaItem.getHeaderRow());
      assertEquals(sequence, fastaItem.getSequenceString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testNumOfFastaItems() {
    FastaReader fastaReader = new FastaReader(isAll);
    List<FastaItem> fastaItemList = new ArrayList<FastaItem>();
    try {
      FastaItem fastaItem = fastaReader.getNextFastaItem();
      while (fastaItem != null) {
        fastaItemList.add(fastaItem);
        fastaItem = fastaReader.getNextFastaItem();
      }
      assertEquals(3, fastaItemList.size());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}

package uzuzjmd.competence.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@ManagedBean(name = "SuggestedCompetenceGrid")
@ViewScoped
public class SuggestedCompetenceGrid implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManagedProperty("#{suggestedCompetenceRows}")
	private List<SuggestedCompetenceRow> suggestedCompetenceRows;	

	public SuggestedCompetenceGrid() {
		suggestedCompetenceRows = new ArrayList<SuggestedCompetenceRow>();
	}


	public List<SuggestedCompetenceRow> getSuggestedCompetenceRows() {
		return suggestedCompetenceRows;
	}

	public void setSuggestedCompetenceRows(
			List<SuggestedCompetenceRow> suggestedCompetenceRows) {
		this.suggestedCompetenceRows = suggestedCompetenceRows;
	}

	@PostConstruct
	public void init() {
		if (suggestedCompetenceRows == null) {
		suggestedCompetenceRows = new ArrayList<SuggestedCompetenceRow>();
		}
	}

}
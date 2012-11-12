package eu.spocseu.gw.de.egvp;

import java.util.logging.Logger;

import eu.spocseu.edeliverygw.messageparts.EvidenceHolder;
import eu.spocseu.edeliverygw.messages.DispatchMessage;
import eu.spocseu.edeliverygw.process.SpocsGWInterface;

public class EGVPSpocsProcessing implements SpocsGWInterface {
	private static Logger LOG = Logger.getLogger(EGVPSpocsProcessing.class
			.getName());

	// TestConfig config;

	@Override
	public EvidenceHolder processDispatch(DispatchMessage dispatchMessage) {
		// LOG.fine("OSCI-EGVP Gateway entry processDispatch");
		// try {
		// Account account = new Account(TestConfig.USERID, config
		// .getAccountKey());
		//
		// LOG.fine("EGVP User ID: " + account.getUserID());
		//
		// InboundMessage inboundMessage = new InboundMessage();
		// OSCIContainer container = inboundMessage.convert(dispatchMessage);
		//
		// Message sendMessage = account
		// .send(container, TestConfig.RECEIVERID);
		// LOG.info("======== EGVP Message was send ============");
		// // ToolboxUtilities.deleteRecursive(account.ge);
		// } catch (Exception ex) {
		// LOG.log(Level.WARNING, "Error:", ex.fillInStackTrace());
		//
		// }
		return null;
	}

	@Override
	public void processEvidence(EvidenceHolder arg0) {
		LOG.fine("OSCI-EGVP Gateway entry processEvidence");
	}

	// TODO which exception interrupts startup
	@Override
	public void initOnStartup() {
		// LOG.info("initalize Pull Task....");
		// try {
		// config = new TestConfig();
		// Application.setConfig(config);
		//
		// Timer timer = new Timer();
		// EGVPPullTask pullTask = new EGVPPullTask(config);
		// timer.schedule(pullTask, 50000, 15000);
		// } catch (Exception e) {
		// LOG.log(Level.WARNING, "ERROR:", e);
		// }

	}
}

package fr.ocroquette.wampoc.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import fr.ocroquette.wampoc.messages.CallErrorMessage;
import fr.ocroquette.wampoc.messages.CallMessage;
import fr.ocroquette.wampoc.messages.CallResultMessage;
import fr.ocroquette.wampoc.messages.MessageMapper;

/***
 * Represents a single RPC call instance with its input, and the output or result after execution.
 *
 */
public class RpcCall {
	public RpcCall(CallMessage msg) {
		callMessage = msg;
	}

	public <InputType> InputType getInput(Class<InputType> inputType)  {
		return callMessage.getPayload(inputType);  
	}

	public <OutputType> void setOutput(OutputType outputValue, Class<OutputType> outputType)  {
		outputJsonElement = new Gson().toJsonTree(outputValue, outputType);  
	}

	public void setError(String errorUri, String errorDesc) {
		hasFailed = true;
		this.errorUri = errorUri;
		this.errorDesc = errorDesc; 
	}

	public void setError(String errorUri, String errorDesc, Object errorDetails) {
		setError(errorUri, errorDesc);
		Gson gson = new Gson();
		this.errorDetails = gson.toJsonTree(errorDetails);
	}

	public boolean hasFailed() {
		return hasFailed;
	}

	public String getResultingJson() {
		if ( ! hasFailed ) {
			CallResultMessage callResultMessage = new CallResultMessage();
			callResultMessage.callId = callMessage.callId;
			callResultMessage.payload = outputJsonElement;
			return MessageMapper.toJson(callResultMessage);
		}
		else {
			CallErrorMessage callErrorMessage = new CallErrorMessage(callMessage.callId, errorUri, errorDesc, errorDetails);
			return MessageMapper.toJson(callErrorMessage);
		}

	}

	protected CallMessage callMessage; 

	protected String inputJsonText;
	protected JsonElement outputJsonElement;

	protected boolean hasFailed = false;

	protected String errorUri;
	protected String errorDesc;
	protected JsonElement errorDetails;

}

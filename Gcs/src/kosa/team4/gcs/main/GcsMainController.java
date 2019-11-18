package kosa.team4.gcs.main;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import kosa.team4.gcs.serviceDBRead.ServiceDBRead;
import kosa.team4.gcs.serviceMagnetOnOff.ServiceMagnetOnOff;
import kosa.team4.gcs.serviceRequestList.ServiceRequestList;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import syk.common.MavJsonListener;
import syk.common.MavJsonMessage;
import syk.gcs.cameraview.CameraView;
import syk.gcs.cameraview.ImageListener;
import syk.gcs.dialog.AlertDialog;
import syk.gcs.hudview.Hud;
import syk.gcs.mapview.FlightMap;
import syk.gcs.mapview.MapListener;
import syk.gcs.messageview.MessageView;
import kosa.team4.gcs.network.Drone;
import kosa.team4.gcs.network.NetworkConfig;

import java.net.URL;
import java.util.ResourceBundle;

public class GcsMainController implements Initializable {
	//---------------------------------------------------------------------------------
	private static Logger logger = LoggerFactory.getLogger(GcsMainController.class);
	//---------------------------------------------------------------------------------

	@FXML public Button btnConnectConfig;
	@FXML public Button btnConnect;
	@FXML public Button btnArm;
	@FXML public TextField txtTakeoffHeight;
	@FXML public Button btnTakeoff;
	@FXML public Button btnLand;
	@FXML public Button btnRtl;
	@FXML public Button btnManual;
	@FXML public CheckBox chkManualMove;
	@FXML public CheckBox chkManualAlt;
	@FXML public TextField txtManualAlt;
	@FXML public Button btnMissionMake;
	@FXML public Button btnMissionClear;
	@FXML public Button btnMissionUpload;
	@FXML public Button btnMissionDownload;
	@FXML public Button btnMissionStart;
	@FXML public Button btnMissionStop;
	@FXML public Button btnGetMissionFromDB;
	@FXML public Button btnSaveMissionToDB;
	@FXML public Button btnFenceMake;
	@FXML public Button btnFenceClear;
	@FXML public Button btnFenceUpload;
	@FXML public Button btnFenceDownload;
	@FXML public Button btnFenceEnable;
	@FXML public Button btnFenceDisable;
	@FXML public Button btnMessageView;
	@FXML public Button btnCameraView;
	@FXML public Button btnNorth;
	@FXML public Button btnSouth;
	@FXML public Button btnEast;
	@FXML public Button btnWest;
	@FXML public Button btnServiceMagnetOnOff;
	@FXML public Button btnServiceRequestList;
	@FXML public TextField TextAName;
	public Drone drone;
	private MqttClient mqttClient;

	//---------------------------------------------------------------------------------
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnConnectConfig.setOnAction(btnConnectConfigEventHandler);
		btnConnect.setOnAction(btnConnectEventHandler);
		btnArm.setOnAction(btnArmEventHandler);
		btnTakeoff.setOnAction(btnTakeoffEventHandler);
		btnTakeoff.setDisable(true);
		btnLand.setOnAction(btnLandEventHandler);
		btnLand.setDisable(true);
		btnRtl.setOnAction(btnRtlEventHandler);
		btnRtl.setDisable(true);
		btnManual.setOnAction(btnManualEventHandler);
		btnManual.setDisable(true);
		btnMissionMake.setOnAction(btnMissionMakeEventHandler);
		btnMissionMake.setDisable(true);
		btnMissionClear.setOnAction(btnMissionClearEventHandler);
		btnMissionClear.setDisable(true);
		btnMissionUpload.setOnAction(btnMissionUploadEventHandler);
		btnMissionUpload.setDisable(true);
		btnMissionDownload.setOnAction(btnMissionDownloadEventHandler);
		btnMissionDownload.setDisable(true);
		btnMissionStart.setOnAction(btnMissionStartEventHandler);
		btnMissionStart.setDisable(true);
		btnMissionStop.setOnAction(btnMissionStopEventHandler);
		btnMissionStop.setDisable(true);
		btnGetMissionFromDB.setOnAction(btnGetMissionFromDBEventHandler);
		btnGetMissionFromDB.setDisable(true);
		btnSaveMissionToDB.setOnAction(btnSaveMissionToDBEventHandler);
		btnSaveMissionToDB.setDisable(true);
		btnFenceMake.setOnAction(btnFenceMakeEventHandler);
		btnFenceMake.setDisable(true);
		btnFenceClear.setOnAction(btnFenceClearEventHandler);
		btnFenceClear.setDisable(true);
		btnFenceUpload.setOnAction(btnFenceUploadEventHandler);
		btnFenceUpload.setDisable(true);
		btnFenceDownload.setOnAction(btnFenceDownloadEventHandler);
		btnFenceDownload.setDisable(true);
		btnFenceEnable.setOnAction(btnFenceEnableEventHandler);
		btnFenceEnable.setDisable(true);
		btnFenceDisable.setOnAction(btnFenceDisableEventHandler);
		btnFenceDisable.setDisable(true);
		btnMessageView.setOnAction(btnMessageViewEventHandler);
		btnCameraView.setOnAction(btnCameraViewEventHandler);
		btnNorth.setOnAction(btnNorthEventHandler);
		btnSouth.setOnAction(btnSouthEventHandler);
		btnEast.setOnAction(btnEastEventHandler);
		btnWest.setOnAction(btnWestEventHandler);
		btnServiceMagnetOnOff.setOnAction(btnServiceMagnetOnOffEventController);
		btnServiceRequestList.setOnAction(btnServiceRequestListEventController);

		drone = new Drone();
		initHud();
		initMessageView();
		initCameraView();
		initFlightMap();
		initConnect();

		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_HEARTBEAT,
				new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonObject) {
						Platform.runLater(() -> {
							btnConnect.setText("연결끊기");
							if (jsonObject.getBoolean("arm")) {
								btnArm.setText("시동끄기");
								btnTakeoff.setDisable(false);
								btnLand.setDisable(false);
								btnRtl.setDisable(false);
								btnManual.setDisable(false);
							} else {
								btnArm.setText("시동걸기");
								btnTakeoff.setDisable(true);
								btnLand.setDisable(true);
								btnRtl.setDisable(true);
								btnManual.setDisable(true);
								if (!drone.flightController.mode.equals(MavJsonMessage.MAVJSON_MODE_STABILIZE)) {
									drone.flightController.sendSetMode(MavJsonMessage.MAVJSON_MODE_STABILIZE);
								}
							}
						});
					}
				}
		);
	}

	//---------------------------------------------------------------------------------
	@FXML
	public StackPane hudPane;
	public Hud hud;

	public void initHud() {
		hud = new Hud();
		hudPane.getChildren().add(hud.ui);

		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_HEARTBEAT,
				new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						hud.controller.setMode(jsonMessage.getString("mode"));
						hud.controller.setArm(jsonMessage.getBoolean("arm"));
					}
				});

		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_GLOBAL_POSITION_INT,
				new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						hud.controller.setAlt(jsonMessage.getDouble("alt"));
					}
				});

		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_ATTITUDE,
				new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						double yaw = jsonMessage.getDouble("yaw");
						if (yaw < 0) {
							yaw += 360;
						}
						hud.controller.setRollPichYaw(
								jsonMessage.getDouble("roll"),
								jsonMessage.getDouble("pitch"),
								yaw
						);
					}
				});

		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_VFR_HUD,
				new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						hud.controller.setSpeed(
								jsonMessage.getDouble("airSpeed"),
								jsonMessage.getDouble("groundSpeed"));
					}
				});

		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_SYS_STATUS,
				new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						hud.controller.setBattery(
								jsonMessage.getDouble("voltageBattery"),
								jsonMessage.getDouble("currentBattery"),
								jsonMessage.getInt("batteryRemaining")
						);
					}
				});

		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_GPS_RAW_INT,
				new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						hud.controller.setGpsFixed(jsonMessage.getString("fix_type"));
					}
				});

		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_STATUSTEXT,
				new MavJsonListener() {
					private String text;

					@Override
					public void receive(JSONObject jsonMessage) {
						hud.controller.setStatusText(jsonMessage.getString("text"));
					}
				});

		hud.controller.btnCamera.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (hud.controller.isVideoOn == false) {
					hud.controller.videoOn();
					drone.camera0.mqttListenerSet(new ImageListener() {
						@Override
						public void receive(byte[] image) {
							hud.controller.videoImage(image);
						}
					});
				} else {
					hud.controller.videoOff();
					drone.camera0.mqttListenerSet(null);
				}
			}
		});
	}

	//---------------------------------------------------------------------------------
	@FXML
	public StackPane messageCamPane;
	public MessageView messageView;

	public void initMessageView() {
		messageView = new MessageView();
		messageCamPane.getChildren().add(messageView.ui);
		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_RECEIVE_MESSAGE_ALL,
				new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						messageView.controller.addReceiveMessage(jsonMessage);
					}
				}
		);
		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_SEND_MESSAGE_ALL,
				new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						messageView.controller.addSendMessage(jsonMessage);
					}
				}
		);
	}

	//---------------------------------------------------------------------------------
	public CameraView cameraView;

	public void initCameraView() {
		cameraView = new CameraView();
		messageCamPane.getChildren().add(cameraView.ui);
		cameraView.ui.setVisible(false);
		drone.camera1.mqttListenerSet(new ImageListener() {
			@Override
			public void receive(byte[] image) {
				cameraView.controller.videoImage(image);
			}
		});
	}

	//---------------------------------------------------------------------------------
	@FXML
	public BorderPane centerBorderPane;
	public FlightMap flightMap;

	public void initFlightMap() {
		flightMap = new FlightMap();
		flightMap.setApiKey("AIzaSyBR_keJURT-bAce2vHKIWKNQTC-GqJWRMI");
		centerBorderPane.setCenter(flightMap.ui);

		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_HEARTBEAT,
				new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						String mode = jsonMessage.getString("mode");
						flightMap.controller.setMode(mode);

						if (drone.flightController.homeLat == 0.0) {
							drone.flightController.sendGetHomePosition();
						}
					}
				});

		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_GLOBAL_POSITION_INT,
				new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						flightMap.controller.setCurrLocation(
								jsonMessage.getDouble("currLat"),
								jsonMessage.getDouble("currLng"),
								jsonMessage.getDouble("heading"));
					}
				});

		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_HOME_POSITION,
				new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						flightMap.controller.setHomePosition(
								jsonMessage.getDouble("homeLat"),
								jsonMessage.getDouble("homeLng"));
						btnMissionMake.setDisable(false);
						btnMissionClear.setDisable(false);
						btnMissionUpload.setDisable(false);
						btnMissionDownload.setDisable(false);
						btnMissionStart.setDisable(false);
						btnMissionStop.setDisable(false);
						btnGetMissionFromDB.setDisable(false);
						btnSaveMissionToDB.setDisable(false);
						btnFenceMake.setDisable(false);
						btnFenceClear.setDisable(false);
						btnFenceUpload.setDisable(false);
						btnFenceDownload.setDisable(false);
						btnFenceEnable.setDisable(false);
						btnFenceDisable.setDisable(false);
					}
				});

		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_MISSION_ACK,
				new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						flightMap.controller.showInfoLabel("미션 업로드 성공");
					}
				});

		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_MISSION_ITEMS,
				new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						flightMap.controller.setMissionItems(jsonMessage.getJSONArray("items"));
						flightMap.controller.showInfoLabel("미션 다운로드 성공");
					}
				});

		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_MISSION_CURRENT,
				new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						flightMap.controller.setMissionCurrent(jsonMessage.getInt("seq"));
					}
				});

		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_FENCE_ACK,
				new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						flightMap.controller.showInfoLabel("펜스 업로드 성공");
					}
				});

		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_FENCE_POINTS,
				new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						flightMap.controller.fenceMapSync(jsonMessage.getJSONArray("points"));
					}
				});
	}

	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnMessageViewEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			if (messageView != null) {
				messageView.ui.setVisible(true);
			}
			if (cameraView != null) {
				cameraView.ui.setVisible(false);
			}
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnCameraViewEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			if (messageView != null) {
				messageView.ui.setVisible(false);
			}
			if (cameraView != null) {
				cameraView.ui.setVisible(true);
			}
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnConnectConfigEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			try {
				Stage dialog = new Stage();
				dialog.setTitle("Network Configuration");
				dialog.initModality(Modality.APPLICATION_MODAL);
				Scene scene = new Scene(NetworkConfig.getInstance().ui);
				scene.getStylesheets().add(GcsMain.class.getResource("style_dark_dialog.css").toExternalForm());
				dialog.setScene(scene);
				dialog.setResizable(false);
				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnConnectEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			if (btnConnect.getText().equals("연결하기")) {
				drone.connect();
			} else {
				drone.disconnect();
				btnConnect.setText("연결하기");
				btnArm.setText("시동걸기");
			}
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnArmEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			if (btnArm.getText().equals("시동걸기")) {
				drone.flightController.sendArm(true);
			} else {
				drone.flightController.sendArm(false);
			}
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnTakeoffEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			float alt = Float.parseFloat(txtTakeoffHeight.getText());
			drone.flightController.sendTakeoff(alt);
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnLandEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			//drone.flightController.sendSetMode("LAND");
			drone.flightController.sendSetMode(MavJsonMessage.MAVJSON_MODE_LAND);
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnRtlEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			//drone.flightController.sendSetMode("RTL");
			drone.flightController.sendSetMode(MavJsonMessage.MAVJSON_MODE_RTL);
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnManualEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			boolean isMove = chkManualMove.isSelected();
			boolean isAlt = chkManualAlt.isSelected();
			double manualAlt = Double.parseDouble(txtManualAlt.getText());

			if (isMove == false && isAlt == true) {
				drone.flightController.sendSetPositionTargetGlobalInt(
						drone.flightController.currLat,
						drone.flightController.currLng,
						manualAlt
				);
				return;
			}

			if (isMove == true) {
				flightMap.controller.mapListenerAdd("manualMove", new MapListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						drone.flightController.sendSetPositionTargetGlobalInt(
								jsonMessage.getDouble("targetLat"),
								jsonMessage.getDouble("targetLng"),
								jsonMessage.getDouble("targetAlt")
						);
					}
				});

				if (isAlt) {
					flightMap.controller.manualMake(manualAlt);
				} else {
					flightMap.controller.manualMake(drone.flightController.alt);
				}
			}
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnMissionMakeEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			flightMap.controller.missionMake();
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnMissionClearEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			flightMap.controller.missionClear();
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnMissionUploadEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			JSONArray jsonArray = flightMap.controller.getMissionItems();
			if (jsonArray.length() < 2) {
				AlertDialog.showOkButton("알림", "미션 아이템 수가 부족합니다.");
			} else {
				drone.flightController.sendMissionUpload(jsonArray);
			}
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnMissionDownloadEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			drone.flightController.sendMissionDownload();
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnMissionStartEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			drone.flightController.sendMissionStart();
			sendMissionStart();
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnMissionStopEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			//drone.flightController.sendSetMode("GUIDED");
			drone.flightController.sendSetMode(MavJsonMessage.MAVJSON_MODE_GUIDED);
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnGetMissionFromDBEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			ServiceDBRead serviceDBRead = new ServiceDBRead();
			serviceDBRead.show();
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnSaveMissionToDBEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			JSONArray jsonArray = flightMap.controller.getMissionItems();
			if (jsonArray.length() < 2) {
				AlertDialog.showOkButton("알림", "미션 아이템 수가 부족합니다.");
			} else {
				sendMqttMission(jsonArray);
				GcsMain.instance.controller.flightMap.controller.showInfoLabel("미션 저장 완료");
			}
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnFenceMakeEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			flightMap.controller.fenceMake();
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnFenceClearEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			flightMap.controller.fenceClear();
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnFenceUploadEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			flightMap.controller.mapListenerAdd("fencePoints", new MapListener() {
				@Override
				public void receive(JSONObject jsonMessage) {
					JSONArray jsonArray = jsonMessage.getJSONArray("points");
					if (jsonArray.length() < 4) {
						AlertDialog.showOkButton("알림", "펜스 포인트 수가 부족합니다.");
					} else {
						drone.flightController.sendFenceUpload(jsonArray);
					}
				}
			});

			flightMap.controller.getFencePoints();
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnFenceDownloadEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			drone.flightController.sendFenceDownload();
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnFenceEnableEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			drone.flightController.sendFenceEnable(true);
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnFenceDisableEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			drone.flightController.sendFenceEnable(false);
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnNorthEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			drone.flightController.sendFindControl(1, 0); //m/s
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnSouthEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			drone.flightController.sendFindControl(-1, 0); //m/s
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnEastEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			drone.flightController.sendFindControl(0, 1); //m/s
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnWestEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			drone.flightController.sendFindControl(0, -1); //m/s
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnServiceMagnetOnOffEventController = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			ServiceMagnetOnOff serviceMagnetOnOff = new ServiceMagnetOnOff();
			serviceMagnetOnOff.show();
		}
	};
	public EventHandler<ActionEvent> btnServiceRequestListEventController = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			ServiceRequestList serviceRequestList = new ServiceRequestList();
			serviceRequestList.show();
		}
	};

	public void initConnect() {
		//======================================================//
		try {
			mqttClient = new MqttClient(NetworkConfig.instance.mqttBrokerConnStr, MqttClient.generateClientId(), null);
			MqttConnectOptions options = new MqttConnectOptions();
			options.setConnectionTimeout(5);
			options.setAutomaticReconnect(true);
			mqttClient.connect(options);
			mqttReceiveFromWeb();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//======================================================//

	}

	public double lat;
	public double lng;
	public String aId;
	public int orderId;
	public String aName = "";
	public String subTopic = "/drone/request/sub";
	public String pubTopic = "/drone/mission/pub";

	public void mqttReceiveFromWeb() {
		mqttClient.setCallback(new MqttCallback() {
			@Override
			public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
				String json = new String(mqttMessage.getPayload());
				logger.info(json);
				handleMessage(json);
			}
			@Override
			public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
			}
			@Override
			public void connectionLost(Throwable throwable) {
			}
		});

		try {
			mqttClient.subscribe(subTopic);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleMessage(String json) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				JSONObject jsonObject = new JSONObject(json);
				String mid = (String) jsonObject.get("msgid");
				if (mid.equals("missioninfo")) {
					aName = (String) jsonObject.get("agencyName");
					AlertDialog.showOkButton("요청접수", "[" + aName + "] 에서 요청이 접수되었습니다.");
					if(AlertDialog.optional.get().getText().equals("승인")) {
						aId = (String) jsonObject.get("agencyId");
						orderId = (Integer) jsonObject.get("orderId");
						TextAName.setText(aName+"("+orderId+")");
						TextAName.autosize();
						System.out.println(aId);
						String waypoint = jsonObject.getString("waypoint");
						if (waypoint.equals("")) {
							lat = (Double) jsonObject.get("lat");
							lng = (Double) jsonObject.get("lng");
							GcsMain.instance.controller.flightMap.controller.setMissionItems(lat, lng);
						} else {
							JSONArray jsonArray = new JSONArray(waypoint);
							GcsMain.instance.controller.flightMap.controller.setMissionItems2(jsonArray);
						}
					}
				} else if (mid.equals("showMission")) {
					String missionWaypoint = jsonObject.getString("waypoint");
					aName = (String) jsonObject.get("agencyName");
					TextAName.setText(aName);
					aId = (String) jsonObject.get("agencyId");
					if (missionWaypoint.equals("")) {
						lat = (Double) jsonObject.get("lat");
						lng = (Double) jsonObject.get("lng");
						GcsMain.instance.controller.flightMap.controller.setMissionItems(lat, lng);
					} else {
						JSONArray jsonArray = new JSONArray(missionWaypoint);
						GcsMain.instance.controller.flightMap.controller.setMissionItems2(jsonArray);
					}
				}
				else if (mid.equals("MISSION_ACTION")) {
					missionAction();
				}
			}
		});
	}

	public void sendMqttMission(JSONArray jsonArray) {
		JSONObject jsonObject = new JSONObject();
		String items = jsonArray.toString();
		System.out.println("보내기");
		jsonObject.put("msgid", "MISSION_UPLOAD");
		jsonObject.put("aID", aId);
		jsonObject.put("items", items);
		String json = jsonObject.toString();
		try {
			mqttClient.publish(pubTopic, json.getBytes(), 0, false);
			System.out.println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMissionStart() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("msgid", "MISSION_START");
		jsonObject.put("aID", aId);
		jsonObject.put("orderId", orderId);
		String json = jsonObject.toString();
		try {
			mqttClient.publish(pubTopic, json.getBytes(), 0, false);
			System.out.println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void missionAction() {
		System.out.println("집가자!!");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("msgid", "MISSION_ACTION");
		jsonObject.put("orderId", orderId);
		String json = jsonObject.toString();
		try {
			mqttClient.publish(pubTopic, json.getBytes(), 0, false);
			System.out.println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}




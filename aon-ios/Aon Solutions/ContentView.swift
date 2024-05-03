//
//  ContentView.swift
//  Aon Solutions
//
//  Created by Alesandro Quirós Gobbato on 03/04/2024.
//

import SwiftUI
import WebKit
import AVKit
import CoreLocation
import PDFKit
import SafariServices
import CodeScanner
import Combine

struct MyConstants {
    static var webURL = URL(string: "http://amillan-app-web.s3-website-eu-west-1.amazonaws.com/")!
    static var aonColour = Color(UIColor(red: 0.0/255.0, green: 36.0/255.0, blue: 105.0/255.0, alpha: 1.0)) // #002469
    static var pdfURL = URL(string: "https://literature.rockwellautomation.com/idc/groups/literature/documents/um/test-um004_-en-p.pdf")!
}

class DataModel: ObservableObject {
    @Published var isScannerVisible: Bool = false
    @Published var scannerResult: String = ""
    @Published var colour: Color = Color.white
}

struct ContentView: View {
    @ObservedObject var dataModel: DataModel
    
    var body: some View {
        ZStack {
            ViewControllerRepresentable(dataModel: dataModel)
                .edgesIgnoringSafeArea(.bottom)
            if dataModel.isScannerVisible {
                CodeScannerView(codeTypes: [.qr], simulatedData: "Some Simulated Data", completion: handleScan)
            }
        }
        .containerRelativeFrame([.horizontal, .vertical])
        .background(dataModel.colour)
    }
    
    func handleScan(result: Result<ScanResult, ScanError>) {
        dataModel.isScannerVisible = false
        // handle the result here
        //print(result)
        switch result {
        case .success(let stringResult):
            print("RESULT: \(stringResult.string)")
            dataModel.scannerResult = stringResult.string
        case .failure(let error):
            print("ERROR: \(error)")
        }
    }
    
}


// MARK: ViewController

class ViewController: UIViewController, UIDocumentPickerDelegate, UIDocumentInteractionControllerDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate, AVCaptureMetadataOutputObjectsDelegate {
    
    var webView: WKWebView!
    
    var dataModel: DataModel
    var cancellable: AnyCancellable?
    
    var locationManager: LocationManager!
    var documentController: UIDocumentInteractionController?
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    init(dataModel: DataModel) {
        self.dataModel = dataModel
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let preference = WKWebpagePreferences()
        preference.preferredContentMode = .mobile
        preference.allowsContentJavaScript = true
        
        let contentController = WKUserContentController()
        contentController.add(self, name: "doStuffMessageHandler")
        
        let configuration = WKWebViewConfiguration()
        configuration.defaultWebpagePreferences = preference
        configuration.userContentController = contentController
        
        webView = WKWebView(frame: view.frame, configuration: configuration)
        webView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        webView.isUserInteractionEnabled = true
        webView.customUserAgent = "solutions.aon.ios"
        view.addSubview(webView)
    
        webView.load(URLRequest(url: MyConstants.webURL))
        
        cancellable = dataModel.$scannerResult.sink { [self] newValue in
            if newValue != "" {
                print("scannerResult changed to \(newValue)")
                sendBarcodeResult(data: newValue)
                dataModel.scannerResult = ""
            }
        }
    }
    
    // MARK: Check Camera Permission
    
    func checkCameraPermission(action: String) {
        let cameraAuthorizationStatus = AVCaptureDevice.authorizationStatus(for: .video)
        switch cameraAuthorizationStatus {
        case .authorized:
            // Camera access granted
            print("Camera access granted")
            if action=="Camera" {
                openCamera()
            } else {
                barcodeScanner()
            }
        case .notDetermined:
            // Request camera access
            AVCaptureDevice.requestAccess(for: .video) { [self] granted in
                if granted {
                    print("Camera access granted")
                    if action=="Camera" {
                        openCamera()
                    } else {
                        barcodeScanner()
                    }
                } else {
                    DispatchQueue.main.async { [self] in
                        // Show alert for denied permission
                        showPermissionDeniedAlert()
                    }
                }
            }
        case .denied, .restricted:
            // Show alert for denied permission
            showPermissionDeniedAlert()
        @unknown default:
            break
        }
    }

    func showPermissionDeniedAlert() {
        let alert = UIAlertController(title: "Camera Access Denied", message: "To take photos or videos, allow access to the camera in your device settings.", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Open Settings", style: .default, handler: { _ in
            if let settingsURL = URL(string: UIApplication.openSettingsURLString) {
                UIApplication.shared.open(settingsURL)
            }
        }))
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
        
        // Present the alert
        UIApplication.shared.keyWindow?.rootViewController?.present(alert, animated: true, completion: nil)
    }
    
    // MARK: Open Camera
    
    func openCamera() {
        DispatchQueue.main.async {
            let imagePicker = UIImagePickerController()
            imagePicker.sourceType = .camera
            
            self.present(imagePicker, animated: true, completion: nil)
        }
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        dismiss(animated: true, completion: nil)
        
        // Extract the selected image
        guard let image = info[UIImagePickerController.InfoKey.originalImage] as? UIImage else {
            print("No image selected")
            return
        }
        
        // Convert the image to data
        guard let imageData = image.jpegData(compressionQuality: 0.5)?.base64EncodedString() else {
            print("Failed to convert image to data")
            return
        }
        
        // Send the image data to the server through JavaScript
        webView.evaluateJavaScript("sendPicture({content: " + imageData + "})")
    }
    
    // MARK: Open Barcode
    
    func barcodeScanner() {
        dataModel.isScannerVisible = true
    }
    
    func sendBarcodeResult(data: String) {
        print("Data: " + data)
        webView.evaluateJavaScript("sendBarcode({content: " + data + "})")
    }
    
    // MARK: Get Position
    
    func sendLocation(data: String) {
        print("LOCATION DATA: " + data)
        webView.evaluateJavaScript("sendLocation({content: " + data + "})")
    }
    
    // MARK: Print File
    
    func printPDF(fromURL url: String) {
        guard let pdfURL = URL(string: url) else {
            print("Invalid URL")
            return
        }
        
        let session = URLSession.shared
        let task = session.dataTask(with: pdfURL) { (data, _, error) in
            if let error = error {
                print("Error downloading PDF: \(error.localizedDescription)")
                return
            }
            
            if let pdfData = data {
                if let pdfDocument = PDFDocument(data: pdfData) {
                    let printController = UIPrintInteractionController.shared
                    let printInfo = UIPrintInfo(dictionary:nil)
                    printInfo.jobName = "Printing PDF"
                    printInfo.outputType = .general
                    
                    printController.printInfo = printInfo
                    printController.showsNumberOfCopies = true
                    printController.printingItem = pdfDocument.dataRepresentation()
                    
                    DispatchQueue.main.async {
                        printController.present(animated: true) { (_, completed, error) in
                            if !completed, let error = error {
                                print("Printing failed: \(error.localizedDescription)")
                            }
                        }
                    }
                } else {
                    print("Invalid PDF data")
                }
            } else {
                print("No data received")
            }
        }
        task.resume()
    }
    
    // MARK: Download Files
    
    func downloadFile(url: URL) {
        let sessionConfig = URLSessionConfiguration.default
        let session = URLSession(configuration: sessionConfig)
        let downloadTask = session.downloadTask(with: url) { (tempURL, response, error) in
            guard let tempURL = tempURL else {
                print("Download error: \(error?.localizedDescription ?? "Unknown error")")
                return
            }
            
            guard let httpResponse = response as? HTTPURLResponse else {
                print("Invalid HTTP response")
                return
            }
            
            // Check if the response contains the expected content type (e.g., "application/pdf")
            if let mimeType = httpResponse.mimeType, mimeType == "application/pdf" {
                // Determine the appropriate file extension
                let fileExtension = "pdf"
                let fileName = "WelcomeToWord.\(fileExtension)"
                let destinationURL = self.getDocumentsDirectory().appendingPathComponent(fileName)
                
                // Check if file already exists at the destination URL
                if FileManager.default.fileExists(atPath: destinationURL.path) {
                    // Remove the existing file
                    do {
                        try FileManager.default.removeItem(at: destinationURL)
                    } catch {
                        print("Error removing existing file: \(error)")
                    }
                }
                
                do {
                    // Move the file to the specified destination with the correct file extension
                    try FileManager.default.moveItem(at: tempURL, to: destinationURL)
                    
                    // Present the document picker for the saved file
                    DispatchQueue.main.async {
                        self.presentDocumentPicker(for: destinationURL)
                    }
                } catch {
                    print("Error moving file: \(error)")
                }
            } else {
                print("Invalid content type. Expected application/pdf.")
            }
        }
        downloadTask.resume()
    }
    
    func presentDocumentPicker(for fileURL: URL) {
        let documentPicker = UIDocumentPickerViewController(url: fileURL, in: .exportToService)
        documentPicker.delegate = self
        documentPicker.modalPresentationStyle = .formSheet
        present(documentPicker, animated: true, completion: nil)
    }
    
    func getDocumentsDirectory() -> URL {
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        return paths[0]
    }
    
    func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
        guard let selectedURL = urls.first else {
            return
        }
        // Handle the selected file URL (e.g., download or process the PDF)
        print("Selected file URL: \(selectedURL)")
    }
    
    func documentPickerWasCancelled(_ controller: UIDocumentPickerViewController) {
        print("Document picker was cancelled")
    }
    
    // MARK: Change Status Bar Colour
    
    func changeStatusBarColour() {
        var colour = dataModel.colour
        if colour == Color.green {
            colour = Color.white
        } else if colour == Color.white {
            colour = Color.black
        } else if colour == Color.black {
            colour = MyConstants.aonColour
        } else {
            colour = Color.green
        }
        dataModel.colour = colour
    }
    
}

// MARK: JavaScript Calls

extension ViewController: WKScriptMessageHandler {
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        if message.name == "doStuffMessageHandler" {
            if let data = message.body as? [String: Any],
               let action = data["action"] as? String {
                print(action)
                switch action {
                case "openCamera":
                    print("CAMERA")
                    checkCameraPermission(action: "Camera")
                case "getPosition":
                    print("LOCATION")
                    self.locationManager = LocationManager()
                    self.locationManager.locationUpdateHandler = { locationString in
                        if let locationString = locationString {
                            print("Location: \(locationString)")
                            self.sendLocation(data: locationString)
                        } else {
                            print("Failed to get location.")
                        }
                    }
                case "openBarcode":
                    print("BARCODE")
                    checkCameraPermission(action: "Barcode")
                case "printFile":
                    print("PRINT")
                    printPDF(fromURL: MyConstants.pdfURL.absoluteString)
                case "downloadFile":
                    print("DOWNLOAD")
                    downloadFile(url: MyConstants.pdfURL)
                case "openFile":
                    print("OPEN")
                    SafariManager.openSafari(url: MyConstants.pdfURL)
                case "changeStatusBarColor":
                    print("CHANGE COLOUR")
                    changeStatusBarColour()
                default:
                    print("DEFAULT")
                }
                /*
                 openCamera
                 getPosition
                 openBarcode
                 printFile
                 downloadFile
                 openFile
                 changeStatusBarColor
                 */
            }
        }
    }
    
}

class LocationManager: NSObject, CLLocationManagerDelegate {
    private var locationManager: CLLocationManager = CLLocationManager()
    var locationUpdateHandler: ((String?) -> Void)?
    
    override init() {
        super.init()
        self.locationManager.delegate = self
        self.locationManager.requestWhenInUseAuthorization() // Request permission to use location
        self.locationManager.startUpdatingLocation() // Start updating location
    }
    
    // CLLocationManagerDelegate method to handle location updates
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }
        // Do something with the current location
        print("Latitude: \(location.coordinate.latitude), Longitude: \(location.coordinate.longitude)")
        let locationString = "\(location.coordinate.latitude), \(location.coordinate.longitude)"
        locationUpdateHandler?(locationString)
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: any Error) {
        // Handle location manager errors
        print("Location manager failed with error: \(error)")
        // Call the completion handler with nil to indicate failure
        locationUpdateHandler?(nil)
    }
    
    // CLLocationManagerDelegate method to handle authorization status change
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        switch status {
        case .authorizedWhenInUse, .authorizedAlways:
            self.locationManager.startUpdatingLocation()
        case .denied, .restricted:
            showPermissionDeniedAlert()
        case .notDetermined:
            // Do nothing if authorization status is not determined yet
            break
        @unknown default:
            // Handle future cases
            break
        }
    }
    
    func showPermissionDeniedAlert() {
        let alert = UIAlertController(title: "Location Access Denied", message: "To determine your location, allow access to the location in your device settings.", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Open Settings", style: .default, handler: { _ in
            if let settingsURL = URL(string: UIApplication.openSettingsURLString) {
                UIApplication.shared.open(settingsURL)
            }
        }))
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
        
        // Present the alert
        UIApplication.shared.keyWindow?.rootViewController?.present(alert, animated: true, completion: nil)
    }
}

protocol BarcodeScannerDelegate: AnyObject {
    func didScanBarcode(_ barcode: String)
}

struct SafariView: UIViewControllerRepresentable {
    let url: URL
    
    func makeUIViewController(context: Context) -> SFSafariViewController {
        return SFSafariViewController(url: url)
    }
    
    func updateUIViewController(_ uiViewController: SFSafariViewController, context: Context) {}
}

struct SafariManager {
    static func openSafari(url: URL) {
        let safariViewController = SafariView(url: url)
        UIApplication.shared.windows.first?.rootViewController?
            .present(UIHostingController(rootView: safariViewController), animated: true, completion: nil)
    }
}

struct ViewControllerRepresentable: UIViewControllerRepresentable {
    @StateObject var dataModel: DataModel
    
    func makeUIViewController(context: Context) -> ViewController {
        // Initialize and return your ViewController
        let controller = ViewController(dataModel: dataModel)
        return controller
    }
    
    func updateUIViewController(_ uiViewController: ViewController, context: Context) {
        // Update the view controller if needed
    }
}

#Preview {
    ContentView(dataModel: DataModel())
}

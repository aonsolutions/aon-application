//
//  Aon_SolutionsApp.swift
//  Aon Solutions
//
//  Created by Alesandro Quirós Gobbato on 03/04/2024.
//

import SwiftUI

@main
struct Aon_SolutionsApp: App {
    @StateObject var dataModel = DataModel()
    
    var body: some Scene {
        WindowGroup {
            ContentView(dataModel: dataModel)
        }
    }
}

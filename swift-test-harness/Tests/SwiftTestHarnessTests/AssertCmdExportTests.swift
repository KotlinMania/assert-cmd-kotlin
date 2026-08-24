import Testing
import AssertCmd

@Suite struct AssertCmdExportTests {
    @Test func testSwiftModuleLoads() throws {
        #expect(Bool(true), "AssertCmd swift module imported cleanly")
    }
}

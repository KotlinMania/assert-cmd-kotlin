import Testing
import AssertCmd

@Suite struct AssertCmdExportTests {
    @Test func exitStatusRetainsCodeAndSuccess() {
        let status = ExitStatus(code: 0, success: true)
        #expect(status.code == 0)
        #expect(status.success)
        #expect(status.toString() == "ExitStatus(code=0, success=true)")
    }

    @Test func exitStatusCopyChangesCodeWithoutMutatingOriginal() {
        let original = ExitStatus(code: 0, success: true)
        let failed = original.copy(code: 7, success: false)
        #expect(original.code == 0)
        #expect(original.success)
        #expect(failed.code == 7)
        #expect(!failed.success)
        #expect(!original.equals(other: failed))
        let equivalent = ExitStatus(code: 7, success: false)
        #expect(failed.equals(other: equivalent))
        #expect(failed.hashCode() == equivalent.hashCode())
    }

    @Test func interruptedStatusPreservesAbsentCode() {
        let interrupted = ExitStatus(code: nil, success: false)
        #expect(interrupted.code == nil)
        #expect(!interrupted.success)
        #expect(interrupted.toString() == "ExitStatus(code=null, success=false)")
    }

    @Test func exitCodePredicateAcceptsOnlyMembers() {
        let predicate = InCodePredicate(expected: [0, 2, 7])
        #expect(predicate.eval(item: 0))
        #expect(predicate.eval(item: 7))
        #expect(!predicate.eval(item: 1))
        #expect(!predicate.eval(item: -1))
        let empty = InCodePredicate(expected: [])
        #expect(!empty.eval(item: 0))
    }
}

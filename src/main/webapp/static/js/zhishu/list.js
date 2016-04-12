$('#add_data_modal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget) // Button that triggered the modal
    var zhishuId = button.data('zhishu')
    var zhishuName = button.data('zhishuname')
    var modal = $(this)
    modal.find('#zhishuId').val(zhishuId)
    modal.find('#zhishuName').val(zhishuName)
})

$('#add_data_batch_modal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget) // Button that triggered the modal
    var zhishuId = button.data('zhishu')
    var zhishuName = button.data('zhishuname')
    var modal = $(this)
    modal.find('#zhishuId').val(zhishuId)
    modal.find('#zhishuName').val(zhishuName)
})
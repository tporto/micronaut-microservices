package br.com.curso.service

import br.com.curso.dto.input.VendaInput
import br.com.curso.dto.output.Parcela
import br.com.curso.dto.output.Venda
import br.com.curso.producer.VendaProducer
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Singleton
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

@Singleton
class VendaService(
  private val veiculoService: VeiculoService,
  private val vendaProducer: VendaProducer,
  private val objectMapper: ObjectMapper
) {

  fun createVenda(vendaInput: VendaInput): Venda {
    val veiculo = veiculoService.getVeiculo(vendaInput.veiculo)
    var parcelas: List<Parcela> = ArrayList<Parcela>()
    val valorParcela = vendaInput.valor.divide(vendaInput.quantidadeParcelas.toBigDecimal())
    var dataVencimento = LocalDate.now().plusMonths(1)

    for (i in 1..vendaInput.quantidadeParcelas) {
      val parcela = Parcela(valorParcela, dataVencimento.toString())

      parcelas = parcelas.plus(parcela)
      dataVencimento = dataVencimento.plusMonths(1)
    }

    val venda = Venda(vendaInput.cliente, veiculo, vendaInput.valor, parcelas)

    println(veiculo)
    confirmVenda(venda)

    return venda
  }

  private fun confirmVenda(venda: Venda) {
    val vendaJSON = objectMapper.writeValueAsString(venda)
    vendaProducer.publishVenda(UUID.randomUUID().toString(), vendaJSON)
  }
}